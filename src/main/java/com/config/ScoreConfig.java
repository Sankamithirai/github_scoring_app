package com.config;

import com.parser.WeightParser;
import com.service.ScoreCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for initializing the {@link ScoreCalculator} bean.
 * Parses scoring weights and decay factor from application properties.
 */
@Configuration
public class ScoreConfig {

    @Value("${score.weights}")
    private String weightsCsv;

    @Value("${score.tau-days}")
    private double tauDays;

    /**
     * Creates a {@link ScoreCalculator} using parsed weights and decay factor.
     *
     * @return configured {@link ScoreCalculator} instance
     */
    @Bean
    public ScoreCalculator scoreCalculator() {
        final Map<String, Double> weights = WeightParser.parse(weightsCsv);
        return ScoreCalculator.from(weights, tauDays);
    }
}
