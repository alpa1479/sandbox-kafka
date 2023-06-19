package edu.sandbox.springbootkafka.consumer.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class ConsumerShellController {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @ShellMethod(value = "Starts read messages from kafka", key = {"start"})
    public void startListener() {
        kafkaListenerEndpointRegistry.start();
    }

    @ShellMethod(value = "Stops read messages from kafka", key = {"stop"})
    public void stopListener() {
        kafkaListenerEndpointRegistry.stop();
    }
}
