package edu.sandbox.springbootkafka.producer.service;

import edu.sandbox.springbootkafka.producer.utils.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleMessageProducer {

    private final KafkaMessageSender sender;
    private final MessageGenerator generator;

    public void send(long amount, boolean messageShouldThrowException) {
        generator.generate(amount, messageShouldThrowException).forEach(sender::send);
    }

    public void send(long amount) {
        generator.generate(amount).forEach(sender::send);
    }
}
