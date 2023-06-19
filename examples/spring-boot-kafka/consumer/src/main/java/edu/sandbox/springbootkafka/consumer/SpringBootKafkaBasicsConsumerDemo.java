package edu.sandbox.springbootkafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootKafkaBasicsConsumerDemo {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootKafkaBasicsConsumerDemo.class, args);
    }
}
