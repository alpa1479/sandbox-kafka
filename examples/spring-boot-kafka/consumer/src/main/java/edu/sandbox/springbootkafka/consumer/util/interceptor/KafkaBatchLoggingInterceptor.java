package edu.sandbox.springbootkafka.consumer.util.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.listener.BatchInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// If we make BatchInterceptor<String, Message> instead of BatchInterceptor<Object, Object> it won't be handled by ConcurrentKafkaListenerContainerFactoryConfigurer.
// Alternative is to set it manually in KafkaConsumerConfig
public class KafkaBatchLoggingInterceptor implements BatchInterceptor<Object, Object> {

    @Override
    public ConsumerRecords<Object, Object> intercept(ConsumerRecords<Object, Object> records, Consumer<Object, Object> consumer) {
        log.info(format(records, consumer));
        return records;
    }

    private String format(ConsumerRecords<Object, Object> records, Consumer<Object, Object> consumer) {
        var builder = new StringBuilder(">>>> Received message batch:\n");
        builder.append(String.format(">>>> batch size = %s, consumer group = %s\n", records.count(), consumer.groupMetadata().groupId()));
        for (var record : records) {
            var line = """ 
                    >>>> topic: %s
                    >>>> partition: %s
                    >>>> offset: %s
                    >>>> key: %s
                    >>>> value: %s
                    >>>> headers: %s

                    """.formatted(record.topic(), record.partition(), record.offset(), record.key(), record.value(), record.headers());
            builder.append(line);
        }
        return builder.toString();
    }
}
