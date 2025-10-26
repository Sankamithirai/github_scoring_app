package com.controller;

import com.repo.ScoreRepo;
import com.service.ScoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes endpoints for fetching and scoring GitHub repositories.
 * Uses {@link ScoringService} to retrieve and compute repository scores.
 */
@RestController
@RequestMapping("/api/repos")
public class ScoringController {

    private final ScoringService scoringService;

    /**
     * Constructs the controller with a {@link ScoringService} dependency.
     *
     * @param scoringService service used to fetch and score repositories
     */
    public ScoringController(final ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    /**
     * Endpoint to retrieve the most popular repositories for a given language and date.
     *
     * @param createdFrom ISO date string indicating the minimum repository creation date
     * @param language    programming language to filter repositories
     * @param limit       maximum number of repositories to return (default 5)
     * @return list of {@link ScoreRepo} objects with computed scores
     */
    @GetMapping("/popular")
    public List<ScoreRepo> popular(
            @RequestParam("created_from") final String createdFrom,
            @RequestParam("language") final String language,
            @RequestParam(name = "limit", defaultValue = "5") final int limit
    ) {
        return scoringService.fetchAndScore(createdFrom, language, limit);
    }
}
