package com.todolist.fileprocessor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "clamav")
@Setter
@Getter
public class ClamAVProperties {
    private String url;
    private Integer port;
}
