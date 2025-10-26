package com.service;

import com.client.GitHubClient;
import com.repo.GitHubRepo;
import com.repo.ScoreRepo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for fetching repositories from GitHub
 * and calculating their popularity scores.
 */
@Service
public class ScoringService {

    private final GitHubClient gitHubClient;
    private final ScoreCalculator scoreCalculator;

    /**
     * Constructs a ScoringService with the required dependencies.
     *
     * @param gitHubClient     client for querying GitHub repositories
     * @param scoreCalculator  calculator used to compute repository scores
     */
    public ScoringService(final GitHubClient gitHubClient, final ScoreCalculator scoreCalculator) {
        this.gitHubClient = gitHubClient;
        this.scoreCalculator = scoreCalculator;
    }

    /**
     * Fetches repositories from GitHub and computes their scores.
     * The results are sorted by score in descending order and limited by count.
     *
     * @param createdFrom earliest creation date
     * @param language    programming language filter
     * @param limit       maximum number of repositories to return
     * @return a list of scored repositories
     */
    public List<ScoreRepo> fetchAndScore(final String createdFrom, final String language, final int limit) {
        final List<Map<String, Object>> raw = gitHubClient.searchRepos(createdFrom, language, limit);

        return raw.stream()
                .map(repo -> {
                    final String fullName = String.valueOf(repo.getOrDefault("full_name", ""));
                    final String url = String.valueOf(repo.getOrDefault("html_url", ""));
                    final String lang = String.valueOf(repo.getOrDefault("language", ""));
                    final int stars = (int) asLong(repo.get("stargazers_count"));
                    final int forks = (int) asLong(repo.get("forks_count"));
                    final String pushedAt = String.valueOf(repo.getOrDefault("pushed_at", ""));
                    final GitHubRepo gh = GitHubRepo.from(repo);
                    final double score = scoreCalculator.score(gh);

                    return new ScoreRepo(fullName, url, lang, stars, forks, pushedAt, score);
                })
                .sorted(Comparator.comparingDouble(ScoreRepo::score).reversed())
                .limit(limit)
                .toList();
    }

    /**
     * Safely converts an object to a long, defaulting to 0 if invalid.
     *
     * @param v object value to convert
     * @return parsed long value or 0 on error
     */
    private long asLong(final Object v) {
        if (v instanceof Number n) return n.longValue();
        try {
            return v == null ? 0L : Long.parseLong(v.toString());
        } catch (final Exception e) {
            return 0L;
        }
    }
}
