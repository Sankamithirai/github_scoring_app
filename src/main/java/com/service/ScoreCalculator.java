package com.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import com.repo.GitHubRepo;

/**
 * Calculates a composite popularity score for GitHub repositories.
 * Combines weighted contributions from stars, forks, and recency.
 */
public class ScoreCalculator {

    private final double wStars, wForks, wRecency;
    private final double tauDays;

    /**
     * Constructs a score calculator with specific weights and decay factor.
     *
     * @param wStars   weight for stars
     * @param wForks   weight for forks
     * @param wRecency weight for recency
     * @param tauDays  time decay constant (in days)
     */
    public ScoreCalculator(final double wStars, final double wForks, final double wRecency, final double tauDays) {
        this.wStars = wStars;
        this.wForks = wForks;
        this.wRecency = wRecency;
        this.tauDays = tauDays;
    }

    /**
     * Factory method to create a {@link ScoreCalculator} from a weight map and decay factor.
     *
     * @param weights  map containing weight keys ("stars", "forks", "recency")
     * @param tauDays  time decay constant
     * @return configured {@link ScoreCalculator} instance
     */
    public static ScoreCalculator from(final Map<String, Double> weights, final double tauDays) {
        final double ws = weights.getOrDefault("stars", 0.5);
        final double wf = weights.getOrDefault("forks", 0.3);
        final double wr = weights.getOrDefault("recency", 0.2);
        return new ScoreCalculator(ws, wf, wr, tauDays);
    }

    /**
     * Computes the popularity score for a given {@link GitHubRepo}.
     *
     * @param r the GitHub repository
     * @return calculated score
     */
    public double score(final GitHubRepo r) {
        final double starsN = Math.log1p(r.stargazers_count());
        final double forksN = Math.log1p(r.forks_count());
        final double recency = recencyDecay(r.pushed_at());
        return wStars * starsN + wForks * forksN + wRecency * recency;
    }

    /**
     * Overloaded version of {@link #score(GitHubRepo)} that accepts a repository map.
     *
     * @param repoMap map containing repository attributes
     * @return calculated score
     */
    public Number score(final Map<String, Object> repoMap) {
        final GitHubRepo gRepo = GitHubRepo.from(repoMap);
        return score(gRepo);
    }

    /**
     * Calculates a time-decay factor based on last push date.
     *
     * @param pushedAtIso ISO 8601 date string of last push
     * @return exponential decay value between 0 and 1
     */
    private double recencyDecay(final String pushedAtIso) {
        if (pushedAtIso == null) return 0.0;
        final Instant pushed = Instant.parse(pushedAtIso);
        final long days = ChronoUnit.DAYS.between(pushed, Instant.now());
        return Math.exp(-days / tauDays);
    }
}
