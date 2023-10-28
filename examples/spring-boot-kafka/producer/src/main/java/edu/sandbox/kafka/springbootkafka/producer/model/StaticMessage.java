package edu.sandbox.kafka.springbootkafka.producer.model;

public record StaticMessage(long id, String text,
                            // if 'true', then RuntimeException will be thrown from KafkaMessageListener
                            boolean shouldThrowException,
                            String nullFieldToTestJacksonProperties) {

    public StaticMessage(long id, String text, boolean shouldThrowException) {
        this(id, text, shouldThrowException, null);
    }
}
