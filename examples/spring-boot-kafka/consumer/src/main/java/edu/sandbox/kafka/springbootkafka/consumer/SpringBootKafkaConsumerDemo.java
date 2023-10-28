package edu.sandbox.kafka.springbootkafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootKafkaConsumerDemo {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootKafkaConsumerDemo.class, args);
    }
}
