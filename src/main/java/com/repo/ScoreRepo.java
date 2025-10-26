package com.repo;

/**
 * Immutable data record representing a scored GitHub repository.
 * Contains repository metadata and its computed popularity score.
 */
public record ScoreRepo(
        String fullName,
        String url,
        String language,
        int stars,
        int forks,
        String pushedAt,
        double score
) { }
