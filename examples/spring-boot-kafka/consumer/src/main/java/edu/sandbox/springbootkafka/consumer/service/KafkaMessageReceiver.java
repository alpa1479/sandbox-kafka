package edu.sandbox.springbootkafka.consumer.service;

import edu.sandbox.springbootkafka.consumer.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageReceiver {

    @KafkaListener(
            id = "consumer-group-1",
            clientIdPrefix = "consumer-1",
            topics = "${application.kafka.topic-name}",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "false"
    )
    public void receive1(@Payload Message message) {
        receiveMessage(message, "consumer-group-1");
    }

    @KafkaListener(
            id = "consumer-group-2",
            clientIdPrefix = "consumer-2",
            topics = "${application.kafka.topic-name}",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "false"
    )
    public void receive2(@Payload Message message) {
        receiveMessage(message, "consumer-group-2");
    }

    public void receiveMessage(Message message, String consumerGroupMessage) {
        var messageId = message.getId();
        log.info(">>>> Received message with id = {} in group = {}", messageId, consumerGroupMessage);
        if (messageId % 10 == 0) {
            throw new RuntimeException(String.format("Received a message multiple of 10 with id %s", messageId));
        }
    }
}
