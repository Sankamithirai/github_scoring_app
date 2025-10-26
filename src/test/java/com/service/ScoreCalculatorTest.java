package com.service;

import com.repo.GitHubRepo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoreCalculatorTest {

    @Test
    void newerRepoScoresHigherWithRecencyWeight() {
        final var scorer = ScoreCalculator.from(Map.of("stars",0.0,"forks",0.0,"recency",1.0), 60);
        final var recent = new GitHubRepo("o/a","", "Java",0,0, Instant.now().toString(), false);
        final var old    = new GitHubRepo("o/b","", "Java",0,0, Instant.now().minusSeconds(200L*24*3600).toString(), false);

        assertTrue(scorer.score(recent) > scorer.score(old));
    }

    @Test
    void starsHaveDiminishingMarginalReturns() {
        final var s = ScoreCalculator.from(Map.of("stars",1.0,"forks",0.0,"recency",0.0), 60);

        final double lowGain  = s.score(repoWithStars(20))  - s.score(repoWithStars(10));
        final double highGain = s.score(repoWithStars(1010)) - s.score(repoWithStars(1000));

        assertTrue(lowGain > highGain, "Adding 10 stars should help more at low counts than at very high counts");
    }

    @Test
    void combinedWeightsAddUpProperly() {
        final var scorer = ScoreCalculator.from(Map.of("stars",0.5,"forks",0.3,"recency",0.2), 60);
        final var r = new GitHubRepo("o/a","", "Java",100,10, Instant.now().toString(), false);
        assertTrue(scorer.score(r) > 0.0);
    }

    private GitHubRepo repoWithStars(final int stars) {
        return new GitHubRepo("o/r","", "Java", stars, 0,
                java.time.Instant.now().toString(), false);
    }

}
