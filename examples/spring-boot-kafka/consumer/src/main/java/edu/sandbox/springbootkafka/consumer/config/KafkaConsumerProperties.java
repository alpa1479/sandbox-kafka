package edu.sandbox.springbootkafka.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.kafka")
public class KafkaConsumerProperties {

    private final String topicName;
    private final DltProperties dlt;
    private final BackOffProperties backOff;

    public String getDltSuffix() {
        return dlt.getSuffix();
    }
}
