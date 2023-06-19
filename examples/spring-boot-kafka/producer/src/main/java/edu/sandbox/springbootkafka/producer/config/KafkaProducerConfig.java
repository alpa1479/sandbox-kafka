package edu.sandbox.springbootkafka.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sandbox.springbootkafka.producer.model.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerProperties producerProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    // Key idea for manual configuration is to use the same ObjectMapper and separate properties that may change, from properties that won't change
    @Bean
    public ProducerFactory<String, Message> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var properties = kafkaProperties.buildProducerProperties();

        // Way for setting properties programmatically, same specified in application.yaml
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        var kafkaProducerFactory = new DefaultKafkaProducerFactory<String, Message>(properties);
        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper)); // without this statement spring will use default object mapper
        return kafkaProducerFactory;
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate(ProducerFactory<String, Message> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // This bean is optional, topic can be created after first message will be sent or manually via kafka interface
    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name(producerProperties.getTopicName())
                .partitions(4)
                .replicas(2)
                .build();
    }
}
