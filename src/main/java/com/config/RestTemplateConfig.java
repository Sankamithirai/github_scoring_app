package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Provides configuration for the {@link RestTemplate} used in the application.
 * Adds default headers required for GitHub API requests.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${github.token:}")
    private String githubToken;

    /**
     * Creates and configures a {@link RestTemplate} with GitHub-specific headers.
     * Includes User-Agent, Accept, and optional Authorization headers.
     *
     * @return configured {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate rt = new RestTemplate();

        final ClientHttpRequestInterceptor ua = (req, body, ex) -> {
            req.getHeaders().add(HttpHeaders.USER_AGENT, "github-scoring-app/1.0");
            req.getHeaders().add(HttpHeaders.ACCEPT, "application/vnd.github+json");
            req.getHeaders().add("X-GitHub-Api-Version", "2022-11-28");
            if (githubToken != null && !githubToken.isBlank()) {
                req.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken.trim());
            }
            return ex.execute(req, body);
        };

        rt.setInterceptors(List.of(ua));
        return rt;
    }
}
