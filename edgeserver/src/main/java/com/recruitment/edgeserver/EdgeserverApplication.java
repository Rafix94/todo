package com.recruitment.edgeserver;

import com.recruitment.edgeserver.filter.RequestLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class EdgeserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeserverApplication.class, args);
    }

    @Bean
    public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder, RequestLoggingFilter requestLoggingFilter) {
        return routeLocatorBuilder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("/todolist/useragent/**")
                        .filters(filterSpec -> filterSpec.rewritePath("/todolist/useragent/(?<segment>.*)","/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config.setName("userAgentCircuitBreaker")))
                        .uri("lb://USERAGENT"))
                .build();
    }

}
