package edu.sandbox.springbootkafka.producer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DynamicMessageJsonReader {

    private final ObjectMapper objectMapper;

    public Object read() {
        var filepath = "/dynamic-message/dynamic-message.json";
        try {
            var resource = getClass().getResource(filepath);
            return objectMapper.readValue(resource, Object.class);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to read resource file by path [%s]", filepath));
        }
    }
}
