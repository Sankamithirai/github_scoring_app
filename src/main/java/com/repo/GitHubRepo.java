package com.repo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Immutable representation of a GitHub repository with essential fields.
 * Unknown JSON properties are ignored during deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepo(
        String full_name,
        String html_url,
        String language,
        int stargazers_count,
        int forks_count,
        String pushed_at,
        boolean archived
) {
    /**
     * Creates a {@link GitHubRepo} instance from a generic map of repository data.
     *
     * @param map a map containing GitHub repository attributes
     * @return a populated {@link GitHubRepo} or {@code null} if the map is null
     */
    public static GitHubRepo from(final Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        return new GitHubRepo(
                (String) map.getOrDefault("full_name", ""),
                (String) map.getOrDefault("html_url", ""),
                (String) map.getOrDefault("language", ""),
                ((Number) map.getOrDefault("stargazers_count", 0)).intValue(),
                ((Number) map.getOrDefault("forks_count", 0)).intValue(),
                (String) map.getOrDefault("pushed_at", ""),
                (Boolean) map.getOrDefault("archived", false)
        );
    }
}
