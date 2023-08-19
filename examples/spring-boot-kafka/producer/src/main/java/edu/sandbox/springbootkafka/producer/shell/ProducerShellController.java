package edu.sandbox.springbootkafka.producer.shell;

import edu.sandbox.springbootkafka.producer.service.MessageSender;
import edu.sandbox.springbootkafka.producer.utils.MessageGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class ProducerShellController {

    private ScheduledExecutorService executor;
    private final MessageSender sender;

    @PostConstruct
    private void init() {
        executor = Executors.newScheduledThreadPool(1);
    }

    @ShellMethod(value = "Send specified amount of messages to kafka", key = {"send"})
    public void sendMessages(@ShellOption(value = "-n", defaultValue = "1") long amount,
                             @ShellOption(value = "-e", defaultValue = "false") boolean messageShouldThrowException) {
        MessageGenerator.generate(amount, messageShouldThrowException).forEach(sender::send);
    }

    @ShellMethod(value = "Send message using scheduled executor with specified period (in seconds)", key = {"start"})
    public void startScheduledExecutor(@ShellOption(value = "-p", defaultValue = "1") long period,
                                       @ShellOption(value = "-e", defaultValue = "false") boolean messageShouldThrowException) {
        if (executor.isTerminated()) {
            executor = Executors.newScheduledThreadPool(1);
        }
        var idGenerator = new AtomicLong(0);
        executor.scheduleAtFixedRate(() -> sender.send(MessageGenerator.generateWithId(idGenerator.incrementAndGet(), messageShouldThrowException)),
                0, period, TimeUnit.SECONDS);
    }

    @ShellMethod(value = "Stops scheduled executor", key = {"stop"})
    public void stopScheduledExecutor() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
}
