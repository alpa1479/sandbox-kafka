package edu.sandbox.kafka.springbootkafka.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootKafkaProducerDemo {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootKafkaProducerDemo.class, args);
    }
}
