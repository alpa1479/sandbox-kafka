package edu.sandbox.springbootkafka.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.kafka")
public class KafkaConsumerProperties {

    private final String topicName;
}
