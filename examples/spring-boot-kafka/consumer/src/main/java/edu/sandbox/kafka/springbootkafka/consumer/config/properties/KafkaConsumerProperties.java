package edu.sandbox.kafka.springbootkafka.consumer.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.kafka.consumer")
public record KafkaConsumerProperties(Topics topics, Dlt dlt, BackOff backOff) {

    public record Topics(String staticMessage, String dynamicMessage) {
    }

    public record Dlt(String suffix) {
    }

    public record BackOff(int maxRetries, Duration maxInterval, Duration initialInterval, double multiplier) {
    }
}
