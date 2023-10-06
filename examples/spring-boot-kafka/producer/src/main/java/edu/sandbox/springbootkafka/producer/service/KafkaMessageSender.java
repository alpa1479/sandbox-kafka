package edu.sandbox.springbootkafka.producer.service;

import edu.sandbox.springbootkafka.producer.config.properties.KafkaProducerProperties;
import edu.sandbox.springbootkafka.producer.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageSender {

    private final KafkaProducerProperties properties;
    private final KafkaTemplate<String, Message> template;

    public void send(Message message) {
        template.send(properties.topicName(), message)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        var offset = result.getRecordMetadata().offset();
                        log.info(">>>> Successfully sent message with id = {}, shouldThrowException = {}, offset = {}", message.id(), message.shouldThrowException(), offset);
                    } else {
                        log.error(">>>> Error during attempt to send message with id = {}", message.id(), exception);
                    }
                });
    }
}
