package edu.sandbox.springbootkafka.consumer.service;

import edu.sandbox.springbootkafka.consumer.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KafkaMessageListener {

    // todo(alpa1479): add payload validation
    @KafkaListener(
            topics = "${spring.kafka.consumer.topic-name}",
            groupId = "consumer-group-1",
            clientIdPrefix = "consumer-1",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "false"
    )
    // Headers added only for debug purposes
    public void onMessage(@Headers Map<String, Object> headers, @Payload List<Message> messages) {
        processBatch(messages);
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic-name}",
            groupId = "consumer-group-2",
            clientIdPrefix = "consumer-2",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "false"
    )
    public void onMessage(@Payload List<Message> messages) {
        processBatch(messages);
    }

    public void processBatch(List<Message> messages) {
        for (Message message : messages) {
            var messageId = message.id();
            if (message.shouldThrowException()) {
                throw new RuntimeException(String.format("Received a message with id %s that should trigger exception", messageId));
            }
        }
    }
}
