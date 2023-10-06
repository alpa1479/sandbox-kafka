package edu.sandbox.springbootkafka.consumer.model;

public record Message(long id, String text,
                      // if 'true', then RuntimeException will be thrown from KafkaMessageListener
                      boolean shouldThrowException) {
}
