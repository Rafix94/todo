package com.todolist.fileprocessor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spaces")
@Setter
@Getter
public class SpacesConfigurationProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

}
