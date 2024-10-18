package com.recruitment.edgeserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient
public class EdgeserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeserverApplication.class, args);
    }

    @Bean
    public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("useragent_route", predicateSpec -> predicateSpec
                        .path("/todolist/useragent/**")
                        .filters(filterSpec -> filterSpec
                                .rewritePath("/todolist/useragent/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(circuitBreakerConfig -> circuitBreakerConfig
                                        .setName("userAgentCircuitBreaker")
                                        .setFallbackUri("forward:/support")
                                )
                        )
                        .uri("http://useragent:8092"))
                .route("http_to_https_redirect", predicateSpec -> predicateSpec
                        .path("/**")
                        .and()
                        .host("todolist.ooguy.com")
                        .and()
                        .predicate(exchange -> {
                            String scheme = exchange.getRequest().getURI().getScheme();
                            return "http".equalsIgnoreCase(scheme);
                        })                        .filters(filterSpec -> filterSpec
                                .redirect(302, "https://todolist.ooguy.com")
                        )
                        .uri("http://localhost"))
                .route("ui_route", predicateSpec -> predicateSpec
                        .path("/**")
                        .uri("http://ui:4200"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(20)).build()).build());
    }
}
