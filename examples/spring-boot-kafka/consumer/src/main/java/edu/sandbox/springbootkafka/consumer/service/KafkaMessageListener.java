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
    public void listenFirstGroup(@Headers Map<String, Object> headers, @Payload List<Message> messages) {
        listen(messages, "consumer-group-1");
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic-name}",
            groupId = "consumer-group-2",
            clientIdPrefix = "consumer-2",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "false"
    )
    public void listenSecondGroup(@Payload List<Message> messages) {
        listen(messages, "consumer-group-2");
    }

    public void listen(List<Message> messages, String consumerGroupMessage) {
        log.info(">>>> Received messages with size = {} in group = {}", messages.size(), consumerGroupMessage);
        for (Message message : messages) {
            var messageId = message.id();
            log.info(">>>> message with id = {} in group = {}", messageId, consumerGroupMessage);
            if (message.shouldThrowException()) {
                throw new RuntimeException(String.format("Received a message with id %s that should trigger exception", messageId));
            }
        }
    }
}
