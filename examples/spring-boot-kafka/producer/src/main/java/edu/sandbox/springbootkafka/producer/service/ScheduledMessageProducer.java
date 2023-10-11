package edu.sandbox.springbootkafka.producer.service;

import edu.sandbox.springbootkafka.producer.utils.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ScheduledMessageProducer {

    private final KafkaMessageSender sender;
    private final MessageGenerator generator;
    private ScheduledExecutorService executor;

    public void start(long period, boolean messageShouldThrowException) {
        initializeExecutorIfRequired();
        var idGenerator = new AtomicLong(0);
        schedule(() -> sender.send(generator.generateWithId(idGenerator.incrementAndGet(), messageShouldThrowException)), period);
    }

    public void start(long period) {
        initializeExecutorIfRequired();
        schedule(() -> sender.send(generator.generate()), period);
    }

    public void stop() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }

    private void initializeExecutorIfRequired() {
        if (executor == null || executor.isTerminated()) {
            executor = Executors.newScheduledThreadPool(1);
        }
    }

    private void schedule(Runnable command, long period) {
        executor.scheduleAtFixedRate(command, 0, period, TimeUnit.SECONDS);
    }
}
