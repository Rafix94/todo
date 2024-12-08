package com.todolist.fileprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableConfigurationProperties
@EnableKafkaStreams
public class FileProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileProcessorApplication.class, args);
    }

}
