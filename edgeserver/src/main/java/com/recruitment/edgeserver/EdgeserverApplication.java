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
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient
public class EdgeserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeserverApplication.class, args);
    }

    @Bean
    @Profile("prod")
    public RouteLocator prodRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("https_redirect", predicateSpec -> predicateSpec
                        .path("/**")
                        .and()
                        .predicate(this::isHttpRequest)
                        .filters(filterSpec -> filterSpec
                                .setStatus(HttpStatus.FOUND)
                                .setResponseHeader("Location", "https://${Host}${RequestPath}")
                        )
                        .uri("no://op"))
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
                .route("task_manager_route", predicateSpec -> predicateSpec
                        .path("/todolist/task-manager/**") // Match path
                        .filters(filterSpec -> filterSpec
                                .rewritePath("/todolist/task-manager/(?<segment>.*)", "/${segment}") // Remove the path prefix
                        )
                        .uri("http://taskmanager:8094")) // Route to the taskmanager service
                .route("ui_route", predicateSpec -> predicateSpec
                        .path("/**")
                        .uri("http://ui:4200"))
                .build();
    }

    private boolean isHttpRequest(ServerWebExchange exchange) {
        return "http".equalsIgnoreCase(exchange.getRequest().getURI().getScheme());
    }

    @Bean
    @Profile("local")
    public RouteLocator localRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
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
                .route("task_manager_route", predicateSpec -> predicateSpec
                        .path("/todolist/task-manager/**")
                        .filters(filterSpec -> filterSpec
                                .rewritePath("/todolist/task-manager/(?<segment>.*)", "/${segment}")
                        )
                        .uri("http://taskmanager:8094"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(20)).build()).build());
    }
}
