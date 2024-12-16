package com.todolist.taskmanager.service;

import com.todolist.FileMetadata;
import com.todolist.ScanDetails;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class ScanResultProcessor {

    private final Environment environment;
    private final FileService fileService;

    @Bean
    public KStream<String, FileMetadata> scanFile(StreamsBuilder streamsBuilder) {
        Map<String, Object> serdeConfig = new HashMap<>();
        serdeConfig.put("spring.json.trusted.packages", environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        try (JsonSerde<FileMetadata> fileJsonSerde = new JsonSerde<>(FileMetadata.class)) {
            fileJsonSerde.configure(serdeConfig, false);

            KStream<String, FileMetadata> sourceStream = streamsBuilder.stream(
                    "file-scanning-results",
                    Consumed.with(Serdes.String(), fileJsonSerde)
            );

            sourceStream.foreach((key, value) -> this.processFile(value));

            return sourceStream;
        }
    }

    private void processFile(FileMetadata fileMetadata) {

        Long fileId = fileMetadata.fileId();

        ScanDetails scanDetails = fileMetadata.scanDetails();

        if (scanDetails != null){
            fileService.updateScanningStatus(fileId, scanDetails.getScanningStatus());
        }
    }
}

