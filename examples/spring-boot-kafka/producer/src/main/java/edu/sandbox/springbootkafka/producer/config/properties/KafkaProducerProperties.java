package edu.sandbox.springbootkafka.producer.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.producer")
public record KafkaProducerProperties(String topicName) {
}
