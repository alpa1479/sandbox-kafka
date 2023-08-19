package edu.sandbox.springbootkafka.producer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private long id;
    private String text;
    // if 'true', then RuntimeException will be thrown from KafkaMessageListener
    private boolean shouldThrowException;
}
