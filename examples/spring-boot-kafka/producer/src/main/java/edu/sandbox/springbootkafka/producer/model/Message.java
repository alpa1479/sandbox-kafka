package edu.sandbox.springbootkafka.producer.model;

public record Message(long id, String text,
                      // if 'true', then RuntimeException will be thrown from KafkaMessageListener
                      boolean shouldThrowException,
                      String nullFieldToTestJacksonProperties) {

    public Message(long id, String text, boolean shouldThrowException) {
        this(id, text, shouldThrowException, null);
    }
}
