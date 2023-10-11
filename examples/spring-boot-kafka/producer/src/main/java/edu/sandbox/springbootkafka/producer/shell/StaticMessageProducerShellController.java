package edu.sandbox.springbootkafka.producer.shell;

import edu.sandbox.springbootkafka.producer.service.ScheduledMessageProducer;
import edu.sandbox.springbootkafka.producer.service.SingleMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class StaticMessageProducerShellController {

    private final SingleMessageProducer singleMessageProducer;
    private final ScheduledMessageProducer scheduledMessageProducer;

    @ShellMethod(value = "Sends specified amount of messages", key = {"send", "s"})
    public void send(@ShellOption(value = "-n", defaultValue = "1") long amount,
                     @ShellOption(value = "-e", defaultValue = "false") boolean messageShouldThrowException) {
        singleMessageProducer.send(amount, messageShouldThrowException);
    }

    @ShellMethod(value = "Sends message using scheduled executor with specified period (in seconds)", key = {"start", "st"})
    public void start(@ShellOption(value = "-p", defaultValue = "1") long period,
                      @ShellOption(value = "-e", defaultValue = "false") boolean messageShouldThrowException) {
        scheduledMessageProducer.start(period, messageShouldThrowException);
    }

    @ShellMethod(value = "Stops scheduled message producer", key = {"stop", "sp"})
    public void stop() throws InterruptedException {
        scheduledMessageProducer.stop();
    }
}
