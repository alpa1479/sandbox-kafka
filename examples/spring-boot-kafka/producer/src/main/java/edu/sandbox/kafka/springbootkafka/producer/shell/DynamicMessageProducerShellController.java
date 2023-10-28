package edu.sandbox.kafka.springbootkafka.producer.shell;

import edu.sandbox.kafka.springbootkafka.producer.service.SingleMessageProducer;
import edu.sandbox.kafka.springbootkafka.producer.service.ScheduledMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class DynamicMessageProducerShellController {

    private final SingleMessageProducer singleMessageProducer;
    private final ScheduledMessageProducer scheduledMessageProducer;

    @ShellMethod(value = "Sends specified amount of messages", key = {"sendd", "sd"})
    public void send(@ShellOption(value = "-n", defaultValue = "1") long amount) {
        singleMessageProducer.send(amount);
    }

    @ShellMethod(value = "Sends message using scheduled executor with specified period (in seconds)", key = {"startd", "std"})
    public void start(@ShellOption(value = "-p", defaultValue = "1") long period) {
        scheduledMessageProducer.start(period);
    }

    @ShellMethod(value = "Stops scheduled message producer", key = {"stopd", "spd"})
    public void stop() throws InterruptedException {
        scheduledMessageProducer.stop();
    }
}
