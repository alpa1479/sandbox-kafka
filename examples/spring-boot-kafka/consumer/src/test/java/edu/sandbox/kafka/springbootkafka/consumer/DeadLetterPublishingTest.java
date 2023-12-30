package edu.sandbox.kafka.springbootkafka.consumer;

import edu.sandbox.kafka.springbootkafka.consumer.model.StaticMessage;
import edu.sandbox.kafka.springbootkafka.consumer.config.properties.KafkaConsumerProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@DisplayName("Given DeadLetterPublishingTest")
public class DeadLetterPublishingTest {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterPublishingTest.class);

    @Container // https://www.testcontainers.org/modules/kafka/
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // connect spring application to testcontainers instance
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    private static KafkaConsumer<String, String> consumer;

    @BeforeAll
    static void setup() {
        // https://docs.spring.io/spring-kafka/docs/3.0.x/reference/html/#testing
        var consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "consumer-group-1", "true");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(List.of("static.message.topic.failures"));
    }

    @AfterAll
    static void close() {
        // close the consumer before shutting down testcontainers instance
        consumer.close();
    }

    @Autowired
    private KafkaConsumerProperties properties;

    @Autowired
    private KafkaOperations<Object, Object> operations;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void initialize() {
        // starting listeners with autoStartup = "false" property
        kafkaListenerEndpointRegistry.start();
    }

    @AfterEach
    void cleanup() {
        // stop listeners to not block a shutdown of the container
        kafkaListenerEndpointRegistry.stop();
    }

    @Test
    @DisplayName("should not produce message to dlt after successful message was sent")
    void shouldNotProduceMessageToDlt() {
        send(new StaticMessage(1, "text", false));

        assertThrows(
                IllegalStateException.class,
                () -> KafkaTestUtils.getSingleRecord(consumer, properties.topics().staticMessage() + properties.dlt().suffix(), Duration.ofSeconds(5)),
                "No records found for topic"
        );
    }

    @Test
    @DisplayName("should produce message to dlt after receiving message that trigger exception")
    void shouldProduceMessageToDlt() {
        send(new StaticMessage(1, "text", true));

        var record = KafkaTestUtils.getSingleRecord(consumer, properties.topics().staticMessage() + properties.dlt().suffix(), Duration.ofSeconds(10));

        var headers = record.headers();
        assertThat(headers).map(Header::key).containsAll(List.of(
                "kafka_dlt-exception-fqcn",
                "kafka_dlt-exception-cause-fqcn",
                "kafka_dlt-exception-message",
                "kafka_dlt-exception-stacktrace",
                "kafka_dlt-original-topic",
                "kafka_dlt-original-partition",
                "kafka_dlt-original-offset",
                "kafka_dlt-original-timestamp",
                "kafka_dlt-original-timestamp-type",
                "kafka_dlt-original-consumer-group"
        ));
        assertThat(new String(headers.lastHeader("kafka_dlt-exception-fqcn").value()))
                .isEqualTo("org.springframework.kafka.listener.ListenerExecutionFailedException");
        assertThat(new String(headers.lastHeader("kafka_dlt-exception-cause-fqcn").value()))
                .isEqualTo("java.lang.RuntimeException");
        assertThat(new String(headers.lastHeader("kafka_dlt-exception-message").value()))
                .contains("Received a message with id 1 that should trigger exception");
    }

    private void send(StaticMessage message) {
        operations.send(properties.topics().staticMessage(), message)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        var offset = result.getRecordMetadata().offset();
                        log.info(">>>> Successfully sent message with id = {}, offset = {}", message.id(), offset);
                    } else {
                        log.error(">>>> Error during attempt to send message with id = {}", message.id(), exception);
                    }
                });
    }
}
