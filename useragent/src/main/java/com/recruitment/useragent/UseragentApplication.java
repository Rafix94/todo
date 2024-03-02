package com.recruitment.useragent;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "UserAgent microservice REST API Documentation",
                description = "UserAgent microservice REST API Documentation",
                version = "1",
                contact = @Contact(name = "Rafał Grześ", email = "r.grzes94@gmail.com")
        )
)
@EnableJpaAuditing
@EnableMethodSecurity
public class UseragentApplication {

    public static void main(String[] args) {
        SpringApplication.run(UseragentApplication.class, args);
    }

}
