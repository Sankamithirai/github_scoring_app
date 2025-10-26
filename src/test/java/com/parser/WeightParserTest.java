package com.parser;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeightParserTest {
    @Test
    void parsesWeightsCsv() {
        Map<String, Double> w = WeightParser.parse("stars:0.4,forks:0.3,recency:0.3");
        assertEquals(0.4, w.get("stars"));
        assertEquals(0.3, w.get("forks"));
        assertEquals(0.3, w.get("recency"));
    }

    @Test
    void toleratesWhitespace() {
        Map<String, Double> w = WeightParser.parse("stars: 0.5 , forks:0.2 ,recency:0.3");
        assertEquals(0.5, w.get("stars"));
    }
}

