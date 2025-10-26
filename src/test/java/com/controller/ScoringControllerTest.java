package com.controller;

import com.repo.ScoreRepo;
import com.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class ScoringControllerTest {

    private MockMvc mvc;

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private ScoringController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void popular_returns200_andJson() throws Exception {
        when(scoringService.fetchAndScore("2024-01-01", "Java", 5)).thenReturn(
                List.of(
                        new ScoreRepo("a/b", "https://gh/ab", "Java", 10, 2, "2025-01-01T00:00:00Z", 42.0),
                        new ScoreRepo("c/d", "https://gh/cd", "Java", 5, 1, "2025-01-01T00:00:00Z", 21.0)
                )
        );

        mvc.perform(get("/api/repos/popular")
                        .param("created_from", "2024-01-01")
                        .param("language", "Java")
                        .param("limit", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].fullName").value("a/b"))
                .andExpect(jsonPath("$[0].language").value("Java"));
    }
}
