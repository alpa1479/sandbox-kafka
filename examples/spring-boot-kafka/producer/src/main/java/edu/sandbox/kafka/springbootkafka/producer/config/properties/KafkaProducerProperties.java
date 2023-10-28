package edu.sandbox.kafka.springbootkafka.producer.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.producer")
public record KafkaProducerProperties(Topics topics) {

    public record Topics(String staticMessage, String dynamicMessage) {
    }
}
