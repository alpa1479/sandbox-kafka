package edu.sandbox.springbootkafka.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sandbox.springbootkafka.consumer.config.properties.KafkaConsumerProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.LogIfLevelEnabled;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties properties;

    @Bean
    public ProducerFactory<Object, Object> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var properties = kafkaProperties.buildProducerProperties();
        var kafkaProducerFactory = new DefaultKafkaProducerFactory<>(properties);
        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper));
        return kafkaProducerFactory;
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var properties = kafkaProperties.buildConsumerProperties();
        var kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(properties);

        // Way for setting properties programmatically, same specified in application.yaml
//        properties.put(JsonDeserializer.TYPE_MAPPINGS, "edu.sandbox.springbootkafka.producer.model.StaticMessage:edu.sandbox.springbootkafka.consumer.model.StaticMessage");
//        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);

        // Without this statement spring will use default object mapper which not includes properties in application.yml
        kafkaConsumerFactory.setValueDeserializer(new JsonDeserializer<>(mapper));
        return kafkaConsumerFactory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory, ConcurrentKafkaListenerContainerFactoryConfigurer configurer
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);

        // ConcurrentMessageListenerContainer customization
        var containerProperties = factory.getContainerProperties();

        var executor = new SimpleAsyncTaskExecutor("k-consumer-");
        executor.setConcurrencyLimit(10);
        var listenerTaskExecutor = new ConcurrentTaskExecutor(executor);
        containerProperties.setListenerTaskExecutor(listenerTaskExecutor);

        containerProperties.setCommitLogLevel(LogIfLevelEnabled.Level.INFO);
        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<Object, Object> kafkaTemplate) {
        // If we don't have a lot of errors in this topic, then we can send errors always to 0 partition,
        // instead of having the same amount of partitions as original topic has.
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // by default without this argument it will send with ".DLT" suffix and to the same partition,
                // see DEFAULT_DESTINATION_RESOLVER inside DeadLetterPublishingRecoverer
                (consumerRecord, exception) -> new TopicPartition(consumerRecord.topic() + properties.dlt().suffix(), 0)
        );
        // If recoverer will not be able to send message to DLT topic it will stop instead of infinite retries
        recoverer.setFailIfSendResultIsError(false);
        return recoverer;
    }

    @Bean
    public CommonErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        var backOff = properties.backOff();

        // ExponentialBackOffWithMaxRetries spreads out attempts over time, taking a little longer between each attempt
        // Set a max for retries below max.poll.interval.ms; default: 5m, as otherwise we trigger a consumer rebalance
        var exponentialBackOff = new ExponentialBackOffWithMaxRetries(backOff.maxRetries());
        exponentialBackOff.setInitialInterval(backOff.initialInterval().toMillis());
        exponentialBackOff.setMultiplier(backOff.multiplier());
        exponentialBackOff.setMaxInterval(backOff.maxInterval().toMillis());

        // By default, DefaultErrorHandler uses FixedBackOff with DEFAULT_MAX_FAILURES - 1 number of retries, where DEFAULT_MAX_FAILURES is equal to 10
        var errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, exponentialBackOff);

        // Option to disable mechanism of retries for specific exceptions (it checks hierarchy of specific exception)
//        errorHandler.addNotRetryableExceptions(Exception.class);

        return errorHandler;
    }
}
