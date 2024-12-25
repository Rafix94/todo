package com.todolist.refinementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class RefinementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefinementServiceApplication.class, args);
    }

}
