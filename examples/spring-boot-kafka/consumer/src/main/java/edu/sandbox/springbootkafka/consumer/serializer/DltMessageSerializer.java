package edu.sandbox.springbootkafka.consumer.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.JacksonUtils;

import java.util.Map;

public class DltMessageSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper = JacksonUtils.enhancedObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, T data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error during serialization", e);
        }
    }

    @Override
    public void close() {
    }
}
