package com.todolist.fileprocessor.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@AllArgsConstructor
public class SpacesConfig {

    private final SpacesConfigurationProperties spacesConfigurationProperties;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                spacesConfigurationProperties.getAccessKey(),
                spacesConfigurationProperties.getSecretKey());

        return S3Client.builder()
                .endpointOverride(URI.create(spacesConfigurationProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.EU_CENTRAL_1)
                .build();
    }
}
