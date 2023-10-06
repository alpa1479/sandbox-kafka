package edu.sandbox.springbootkafka.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sandbox.springbootkafka.producer.model.Message;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Message> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var properties = kafkaProperties.buildProducerProperties();
        var kafkaProducerFactory = new DefaultKafkaProducerFactory<String, Message>(properties);

        // Way for setting properties programmatically, same specified in application.yaml
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Without this statement spring will use default object mapper which not includes properties in application.yml
        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper));
        return kafkaProducerFactory;
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate(ProducerFactory<String, Message> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
