package edu.sandbox.springbootkafka.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootKafkaBasicsProducerDemo {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootKafkaBasicsProducerDemo.class, args);
    }
}
