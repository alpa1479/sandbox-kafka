package edu.sandbox.kafka.springbootkafka.consumer.model;

public record StaticMessage(long id, String text,
                            // if 'true', then RuntimeException will be thrown from KafkaMessageListener
                            boolean shouldThrowException) {
}
