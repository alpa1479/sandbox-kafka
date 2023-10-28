package edu.sandbox.kafka.springbootkafka.producer.utils;

import edu.sandbox.kafka.springbootkafka.producer.model.StaticMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.LongStream;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class MessageGenerator {

    private final DynamicMessageJsonReader reader;

    public Object generate() {
        return reader.read();
    }

    public List<Object> generate(long amount) {
        return LongStream.range(1, amount + 1)
                .mapToObj(unused -> reader.read())
                .toList();
    }

    public List<StaticMessage> generate(long amount, boolean messageShouldThrowException) {
        return LongStream.range(1, amount + 1)
                .mapToObj(index -> new StaticMessage(index, format("message with id = %d", index), messageShouldThrowException))
                .toList();
    }

    public StaticMessage generateWithId(long id, boolean messageShouldThrowException) {
        return new StaticMessage(id, format("message with id = %d", id), messageShouldThrowException);
    }
}
