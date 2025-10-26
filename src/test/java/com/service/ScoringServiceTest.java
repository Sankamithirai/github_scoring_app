package com.service;

import com.client.GitHubClient;
import com.repo.GitHubRepo;
import com.repo.ScoreRepo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScoringServiceTest {

    @Test
    void fetchAndScoreSortsDescendingAndLimits() {
        GitHubClient client = mock(GitHubClient.class);
        ScoreCalculator calculator = mock(ScoreCalculator.class);

        ScoringService service = new ScoringService(client, calculator);

        when(client.searchRepos("2024-01-01", "Java", 5)).thenReturn(
                List.of(
                        Map.of("full_name","a/low","html_url","https://gh/a","language","Java","stargazers_count", 3, "forks_count", 1, "pushed_at", Instant.now().toString(),"archived", false),
                        Map.of("full_name","b/high","html_url","https://gh/b","language","Java","stargazers_count", 10,"forks_count", 4, "pushed_at", Instant.now().toString(),"archived", false),
                        Map.of("full_name","c/mid","html_url","https://gh/c","language","Java","stargazers_count", 5, "forks_count", 0, "pushed_at", Instant.now().toString(),"archived", false)
                )
        );

        when(calculator.score((GitHubRepo) any())).thenAnswer(inv -> {
            GitHubRepo r = inv.getArgument(0);
            double stars = r.stargazers_count();
            double forks = r.forks_count();
            return stars + forks * 0.1;
        });

        List<ScoreRepo> out = service.fetchAndScore("2024-01-01", "Java", 5);

        assertEquals(3, out.size());
        assertEquals("b/high", out.get(0).fullName(), "highest score should be first");

        verify(client, times(1)).searchRepos("2024-01-01", "Java", 5);
        verifyNoMoreInteractions(client);
        verify(calculator, times(3)).score((GitHubRepo) any());
    }
}
