package edu.sandbox.kafka.springbootkafka.consumer.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class ConsumerShellController {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @ShellMethod(value = "Starts read messages from kafka", key = {"start", "st"})
    public void startListener() {
        kafkaListenerEndpointRegistry.start();
    }

    @ShellMethod(value = "Stops read messages from kafka", key = {"stop", "sp"})
    public void stopListener() {
        kafkaListenerEndpointRegistry.stop();
    }
}
