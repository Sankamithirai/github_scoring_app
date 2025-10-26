package com.parser;

import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class for parsing scoring weights from a CSV-style string.
 */
public class WeightParser {

    /**
     * Parses a CSV string into a map of weight keys and double values.
     *
     * @param weightsCsv the input string containing weights in key:value format
     * @return a map of parsed weights
     */
    public static Map<String, Double> parse(final String weightsCsv) {
        return Arrays.stream(weightsCsv.split(","))
                .map(kv -> kv.split(":"))
                .filter(a -> a.length == 2)
                .collect(Collectors.toMap(
                        a -> a[0].trim().toLowerCase(),
                        a -> Double.parseDouble(a[1].trim())
                ));
    }
}
