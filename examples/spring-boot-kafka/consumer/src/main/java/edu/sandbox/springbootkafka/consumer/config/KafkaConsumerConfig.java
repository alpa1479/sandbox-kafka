package edu.sandbox.springbootkafka.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sandbox.springbootkafka.consumer.model.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private static final String DLT_TOPIC_SUFFIX = ".dlt";

    private final KafkaConsumerProperties consumerProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate(ProducerFactory<String, Message> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Message> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var properties = kafkaProperties.buildProducerProperties();
        // Way for setting properties programmatically, same specified in application.yaml
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        var kafkaProducerFactory = new DefaultKafkaProducerFactory<String, Message>(properties);
        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper));
        return kafkaProducerFactory;
    }

    @Bean
    public ConsumerFactory<String, Message> consumerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var properties = kafkaProperties.buildConsumerProperties();
        // Way for setting properties programmatically, same specified in application.yaml
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        properties.put(JsonDeserializer.TYPE_MAPPINGS, "edu.sandbox.springbootkafka.producer.model.Message:edu.sandbox.springbootkafka.consumer.model.Message");
//        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_000);

        var kafkaConsumerFactory = new DefaultKafkaConsumerFactory<String, Message>(properties);
        kafkaConsumerFactory.setValueDeserializer(new JsonDeserializer<>(mapper));
        return kafkaConsumerFactory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Message>> kafkaListenerContainerFactory(
            ConsumerFactory<String, Message> consumerFactory, DefaultErrorHandler errorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Message>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(errorHandler);

        var containerProperties = factory.getContainerProperties();
        containerProperties.setIdleBetweenPolls(1_000);
        containerProperties.setPollTimeout(1_000);

        var executor = new SimpleAsyncTaskExecutor("k-consumer-");
        executor.setConcurrencyLimit(10);

        var listenerTaskExecutor = new ConcurrentTaskExecutor(executor);
        containerProperties.setListenerTaskExecutor(listenerTaskExecutor);
        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Message> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (consumerRecord, exception) -> new TopicPartition(consumerRecord.topic() + DLT_TOPIC_SUFFIX, consumerRecord.partition())
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        var errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer);
        errorHandler.addNotRetryableExceptions(Exception.class);
        return errorHandler;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name(consumerProperties.getTopicName() + DLT_TOPIC_SUFFIX)
                .partitions(4)
                .replicas(2)
                .build();
    }
}
