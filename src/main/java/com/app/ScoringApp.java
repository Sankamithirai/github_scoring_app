package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The {@code ScoringApp} class serves as the main entry point
 * for the GitHub Repository Scoring Application.
 *
 * <p>This Spring Boot application analyzes and ranks public GitHub
 * repositories based on their popularity metrics such as stars,
 * forks, and recent activity. It exposes REST endpoints to fetch
 * and score repositories dynamically.
 */
@SpringBootApplication(scanBasePackages = "com")
public class ScoringApp {
    /**
     *
     * <p>Delegates to Spring Boot’s {@link SpringApplication#run}
     * to start the application context and auto-configure all components.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {
        SpringApplication.run(ScoringApp.class, args);
    }
}
