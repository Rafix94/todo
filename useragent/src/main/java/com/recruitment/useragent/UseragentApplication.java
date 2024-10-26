package com.recruitment.useragent;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "UserAgent Microservice API Documentation",
                description = "This API documentation provides information about the endpoints available in the UserAgent microservice. Use this documentation to understand how to interact with the UserAgent microservice.",
                version = "1.0",
                contact = @Contact(
                        name = "Rafał Grześ",
                        email = "r.grzes94@gmail.com",
                        url = "https://github.com/example/useragent-microservice"
                )
        )
)
@EnableDiscoveryClient
public class UseragentApplication {

    public static void main(String[] args) {
        SpringApplication.run(UseragentApplication.class, args);
    }

}