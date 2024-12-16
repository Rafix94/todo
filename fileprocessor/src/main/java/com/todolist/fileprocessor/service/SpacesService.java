package com.todolist.fileprocessor.service;

import com.todolist.fileprocessor.config.SpacesConfigurationProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SpacesService {

    private final S3Client s3Client;
    private final SpacesConfigurationProperties spacesConfigurationProperties;

    public void uploadFile(String key, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(spacesConfigurationProperties.getBucketName())
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }

    public InputStream downloadFile(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(spacesConfigurationProperties.getBucketName())
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    public List<String> listFiles() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(spacesConfigurationProperties.getBucketName())
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

}
