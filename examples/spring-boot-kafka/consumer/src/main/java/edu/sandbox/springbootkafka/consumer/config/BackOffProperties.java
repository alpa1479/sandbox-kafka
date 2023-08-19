package edu.sandbox.springbootkafka.consumer.config;

import lombok.Data;

import java.time.Duration;

@Data
public class BackOffProperties {

    private final int maxRetries;
    private final Duration maxInterval;
    private final Duration initialInterval;
    private final double multiplier;
}
