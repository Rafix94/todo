package com.todolist.fileprocessor.service;

import com.todolist.fileprocessor.antivirusScanning.ClamAVResponseParser;
import com.todolist.fileprocessor.antivirusScanning.ClamAVService;
import com.todolist.fileprocessor.model.FileMetadata;
import com.todolist.fileprocessor.model.ScanDetails;
import com.todolist.fileprocessor.model.ScanningResult;
import com.todolist.fileprocessor.model.ScanningStatus;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.io.IOException;
import java.io.InputStream;


@Configuration
@AllArgsConstructor
public class FileScanner {
    private final SpacesService spacesService;
    private final ClamAVService clamAVService;

    @Bean
    public KStream<String, FileMetadata> scanFile(StreamsBuilder streamsBuilder) {
        try (JsonSerde<FileMetadata> fileJsonSerde = new JsonSerde<>(FileMetadata.class)) {

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

            if(name != null ){
                scanDetails.setScanningResult(ScanningResult.INFECTED);
                scanDetails.setVirus(name);
            } else {
                scanDetails.setScanningResult(ScanningResult.CLEAN);
            }
        } catch (IOException e) {
            scanDetails.setScanningResult(ScanningResult.UNKNOWN);
        }
        scanDetails.setScanningStatus(ScanningStatus.SCANNED);
        return fileMetadata;
    }
}
