package edu.sandbox.springbootkafka.producer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.kafka")
public class KafkaProducerProperties {

    private final String topicName;
}
