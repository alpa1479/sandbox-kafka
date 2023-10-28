package edu.sandbox.kafka.springbootkafka.producer.service;

import edu.sandbox.kafka.springbootkafka.producer.config.properties.KafkaProducerProperties;
import edu.sandbox.kafka.springbootkafka.producer.model.StaticMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageSender {

    private final KafkaProducerProperties properties;
    private final KafkaOperations<Object, Object> operations;

    public void send(StaticMessage message) {
        operations.send(properties.topics().staticMessage(), message)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        var offset = result.getRecordMetadata().offset();
                        log.info(">>>> Successfully sent message with id = {}, shouldThrowException = {}, offset = {}", message.id(), message.shouldThrowException(), offset);
                    } else {
                        log.error(">>>> Error during attempt to send message with id = {}", message.id(), exception);
                    }
                });
    }

    public void send(Object message) {
        operations.send(properties.topics().dynamicMessage(), message)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        var offset = result.getRecordMetadata().offset();
                        log.info(">>>> Successfully sent message, offset = {}", offset);
                    } else {
                        log.error(">>>> Error during attempt to send message", exception);
                    }
                });
    }
}
