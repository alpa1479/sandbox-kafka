package edu.sandbox.springbootkafka.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sandbox.springbootkafka.consumer.model.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
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
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties properties;

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
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        properties.put(JsonDeserializer.TYPE_MAPPINGS, "edu.sandbox.springbootkafka.producer.model.Message:edu.sandbox.springbootkafka.consumer.model.Message");
//        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);

        // max time after which consumer will be considered as not available for kafka
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30_000);

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
        // to enable batch processing
        factory.setBatchListener(true);
        // the number of consumers to create
        factory.setConcurrency(1);
        factory.setCommonErrorHandler(errorHandler);

        var containerProperties = factory.getContainerProperties();
        // the interval to sleep between polling cycles
        containerProperties.setIdleBetweenPolls(1_000);
        // max time to block in the consumer waiting for records
        containerProperties.setPollTimeout(1_000);

        var executor = new SimpleAsyncTaskExecutor("k-consumer-");
        executor.setConcurrencyLimit(10);

        var listenerTaskExecutor = new ConcurrentTaskExecutor(executor);
        containerProperties.setListenerTaskExecutor(listenerTaskExecutor);
        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Message> kafkaTemplate) {
        // If we don't have a lot of errors in this topic, then we can send errors always to 0 partition,
        // instead of having the same amount of partitions as original topic has.
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // by default without this argument it will send with ".DTL" suffix and to the same partition,
                // see DEFAULT_DESTINATION_RESOLVER inside DeadLetterPublishingRecoverer
                (consumerRecord, exception) -> new TopicPartition(consumerRecord.topic() + properties.getDltSuffix(), 0)
        );
    }

    // todo(alpa1479): check RetryListener interface.
    // todo(alpa1479): add logging of exceptions
    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        var backOff = properties.getBackOff();

        // ExponentialBackOffWithMaxRetries spreads out attempts over time, taking a little longer between each attempt
        // Set a max for retries below max.poll.interval.ms; default: 5m, as otherwise we trigger a consumer rebalance
        var exponentialBackOff = new ExponentialBackOffWithMaxRetries(backOff.getMaxRetries());
        exponentialBackOff.setInitialInterval(backOff.getInitialInterval().toMillis());
        exponentialBackOff.setMultiplier(backOff.getMultiplier());
        exponentialBackOff.setMaxInterval(backOff.getMaxInterval().toMillis());

        // By default, DefaultErrorHandler uses FixedBackOff with DEFAULT_MAX_FAILURES - 1 number of retries, where DEFAULT_MAX_FAILURES is equal to 10
        var errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, exponentialBackOff);

        // Option to disable mechanism of retries for specific exceptions (it checks hierarchy of specific exception)
//        errorHandler.addNotRetryableExceptions(Exception.class);

        return errorHandler;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name(properties.getTopicName() + properties.getDltSuffix())
                .partitions(1)
                .replicas(2)
                // We can se longer retention for Dead Letter Topic, allowing for more time to troubleshoot
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(properties.getDlt().getRetention().toMillis()))
                .build();
    }
}
