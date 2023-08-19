package edu.sandbox.springbootkafka.consumer.config;

import lombok.Data;

import java.time.Duration;

@Data
public class DltProperties {

    private final String suffix;
    private final Duration retention;
}
