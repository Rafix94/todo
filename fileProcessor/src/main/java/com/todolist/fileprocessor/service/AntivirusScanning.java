package com.todolist.fileprocessor.service;

import com.todolist.fileprocessor.antivirusScanning.ClamAVService;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AntivirusScanning {
    private final SpacesService spacesService;
    private final ClamAVService clamAVService;

    @Autowired
    public void scanFile(StreamsBuilder streamBuilder) {
        KStream<String, String> sourceStream = streamBuilder.stream("scan", Consumed.with(Serdes.String(), Serdes.String()));

        sourceStream.mapValues(this::scanFiles);

        sourceStream.to("scan-result");
    }

    public String scanFiles(String file) {
        System.out.println(file);
        return file;
    }


//
//    @KafkaListener(topics = {"ad", "ww"}, groupId = "asd")
//    public void scanFile(File file) {
//        InputStream inputStream = spacesService.downloadFile(file.fileKey());
//
//        clamAVService.scanFile(inputStream.);
//    }

}
