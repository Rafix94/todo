package com.todolist.fileprocessor.service;

import com.todolist.ScanDetails;
import com.todolist.ScanningStatus;
import com.todolist.fileprocessor.antivirusScanning.ClamAVResponseParser;
import com.todolist.fileprocessor.antivirusScanning.ClamAVService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;
import com.todolist.FileMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class FileScanner {
    private final SpacesService spacesService;
    private final ClamAVService clamAVService;
    private final Environment environment;

    @Bean
    public KStream<String, FileMetadata> scanFile(StreamsBuilder streamsBuilder) {
        Map<String, Object> serdeConfig = new HashMap<>();
        serdeConfig.put("spring.json.trusted.packages", environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        try (JsonSerde<FileMetadata> fileJsonSerde = new JsonSerde<>(FileMetadata.class)) {
            fileJsonSerde.configure(serdeConfig, false);

            KStream<String, FileMetadata> sourceStream = streamsBuilder.stream(
                    "file-scanning-requests",
                    Consumed.with(Serdes.String(), fileJsonSerde)
            );

            sourceStream
                    .mapValues(this::processFile)
                    .to("file-scanning-results", Produced.with(Serdes.String(), fileJsonSerde));

            return sourceStream;
        }
    }

    private FileMetadata processFile(FileMetadata fileMetadata) {
        InputStream fileStream = spacesService.downloadFile(fileMetadata.s3Key());
        ScanDetails scanDetails = fileMetadata.scanDetails();

        try {
            String scanResult = clamAVService.scan(fileStream);
            String name = ClamAVResponseParser.extractVirusName(scanResult);

            if( name != null ){
                scanDetails.setScanningStatus(ScanningStatus.INFECTED);
                scanDetails.setVirus(name);
            } else {
                scanDetails.setScanningStatus(ScanningStatus.CLEAN);
            }
        } catch (IOException e) {
            scanDetails.setScanningStatus(ScanningStatus.SCAN_FAILED);
        }
        return fileMetadata;
    }
}
