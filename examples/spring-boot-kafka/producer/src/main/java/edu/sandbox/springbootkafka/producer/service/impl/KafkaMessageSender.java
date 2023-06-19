package edu.sandbox.springbootkafka.producer.service.impl;

import edu.sandbox.springbootkafka.producer.config.KafkaProducerProperties;
import edu.sandbox.springbootkafka.producer.model.Message;
import edu.sandbox.springbootkafka.producer.service.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageSender implements MessageSender {

    private final KafkaProducerProperties producerProperties;
    private final KafkaTemplate<String, Message> template;

    @Override
    public void send(Message message) {
        template.send(producerProperties.getTopicName(), message)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        var offset = result.getRecordMetadata().offset();
                        log.info(">>>> Successfully sent message with id = {}, offset = {}", message.getId(), offset);
                    } else {
                        log.error(">>>> Error during attempt to send message with id = {}", message.getId(), exception);
                    }
                });
    }
}
