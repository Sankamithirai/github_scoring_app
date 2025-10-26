package com.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight GitHub REST client.
 * <p>Builds a search query, calls GitHub’s repositories search API, and returns
 * a trimmed list of repo maps containing only the fields the scoring layer needs.</p>
 */
@Component
public class GitHubClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    /**
     * Creates the client with a {@link RestTemplate} and base URL.
     *
     * @param restTemplate Spring HTTP client
     * @param baseUrl      GitHub API base URL (defaults to https://api.github.com)
     */
    public GitHubClient(
            final RestTemplate restTemplate,
            @Value("${github.base-url:https://api.github.com}") final String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Convenience overload: searches by date and language with a hard limit.
     *
     * @param createdFrom ISO date (YYYY-MM-DD)
     * @param language    GitHub language qualifier
     * @param limit       max number of repos to return
     * @return trimmed repo maps
     */
    public List<Map<String, Object>> searchRepos(final String createdFrom, final String language, final int limit) {
        return searchRepos(createdFrom, language, null, limit);
    }

    /**
     * Performs a single-page search with minimal encoding and simple fallbacks.
     *
     * @param createdFrom ISO date (YYYY-MM-DD)
     * @param language    GitHub language qualifier
     * @param freeText    optional free text added to the query
     * @param limit       max number of repos to return
     * @return trimmed repo maps containing only essential fields
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchRepos(
            final String createdFrom,
            final String language,
            final String freeText,
            final int limit
    ) {
        final int perPage = Math.max(1, Math.min(100, limit));
        final String lang = isBlank(language) ? "" : ("language:" + language.trim());
        final String date = isBlank(createdFrom) ? "" : ("pushed:>=" + createdFrom.trim());
        final String free = isBlank(freeText) ? "" : freeText.trim();

        final List<String> candidates = new ArrayList<>();
        if (!isBlank(date) && !isBlank(lang)) {
            candidates.add(join(free, date, lang));
        }
        if (!isBlank(lang)) {
            candidates.add(join(free, lang));
        }
        if (candidates.isEmpty()) {
            candidates.add("language:Java");
        }

        for (final String rawQ : candidates) {
            final String q = encodeQ(rawQ);
            final String uri = baseUrl + "/search/repositories"
                    + "?q=" + q
                    + "&sort=stars&order=desc"
                    + "&per_page=" + perPage
                    + "&page=1";

            try {
                System.out.println("Calling GitHub: " + uri + "   (raw q='" + rawQ + "')");

                final Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
                if (response == null) continue;

                final Object itemsObj = response.get("items");
                if (!(itemsObj instanceof List<?> rawItems) || rawItems.isEmpty()) {
                    System.out.println("Received 0 items for q='" + rawQ + "'. Trying next candidate...");
                    continue;
                }

                final List<Map<String, Object>> trimmed = new ArrayList<>();
                for (final Object it : rawItems) {
                    if (it instanceof Map<?, ?> m) {
                        trimmed.add(trimRepo((Map<String, Object>) m));
                        if (trimmed.size() == limit) break;
                    }
                }
                System.out.println("Returning " + trimmed.size() + " repos.");
                return trimmed;

            } catch (final HttpStatusCodeException ex) {
                System.err.println("GitHub search failed: " + ex.getStatusCode()
                        + " - " + ex.getResponseBodyAsString());
            } catch (final Exception e) {
                System.err.println("Unexpected error during GitHub search: " + e);
            }
        }

        return List.of();
    }

    /**
     * Null/blank helper.
     *
     * @param string input string
     * @return true if null or blank
     */
    private boolean isBlank(final String string) { return string == null || string.trim().isEmpty(); }

    /**
     * Joins non-blank parts with single spaces.
     *
     * @param parts string parts
     * @return joined string
     */
    private String join(final String... parts) {
        final StringBuilder b = new StringBuilder();
        for (final String p : parts) {
            if (!isBlank(p)) {
                if (!b.isEmpty()) b.append(' ');
                b.append(p.trim());
            }
        }
        return b.toString();
    }

    /**
     * Minimal encoding for GitHub {@code q}: space→'+', '>'→'%3E'.
     * '=' is intentionally left as-is so {@code '>='} becomes {@code '%3E='}.
     *
     * @param raw unencoded query
     * @return minimally encoded query
     */
    private String encodeQ(final String raw) {
        String s = raw.trim().replaceAll("\\s+", " ");
        s = s.replace(" ", "+");
        s = s.replace(">", "%3E");
        return s;
    }

    /**
     * Copies only essential fields used downstream for scoring.
     *
     * @param repo full GitHub repo map
     * @return trimmed map with essential keys
     */
    private Map<String, Object> trimRepo(final Map<String, Object> repo) {
        final Map<String, Object> out = new LinkedHashMap<>();
        copy(repo, out, "id");
        copy(repo, out, "full_name");
        copy(repo, out, "html_url");
        copy(repo, out, "language");
        copy(repo, out, "stargazers_count");
        copy(repo, out, "forks_count");
        copy(repo, out, "pushed_at");
        copy(repo, out, "archived");
        return out;
    }

    /**
     * Safe put-if-present copy for a single key.
     *
     * @param src source map
     * @param dst destination map
     * @param key key to copy
     */
    private void copy(final Map<String, Object> src, final Map<String, Object> dst, final String key) {
        if (src.containsKey(key)) dst.put(key, src.get(key));
    }
}
