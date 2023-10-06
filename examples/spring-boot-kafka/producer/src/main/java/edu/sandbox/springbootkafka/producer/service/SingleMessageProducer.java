package edu.sandbox.springbootkafka.producer.service;

import edu.sandbox.springbootkafka.producer.utils.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleMessageProducer {

    private final KafkaMessageSender sender;

    public void send(long amount, boolean messageShouldThrowException) {
        MessageGenerator.generate(amount, messageShouldThrowException).forEach(sender::send);
    }
}
