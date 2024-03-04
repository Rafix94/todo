package com.recruitment.edgeserver.filter;

import com.recruitment.edgeserver.entity.RequestLogEntity;
import com.recruitment.edgeserver.repository.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Order(1)
@Component
public class RequestLoggingFilter implements GlobalFilter {

    private final RequestLogRepository requestLogRepository;

    @Autowired
    public RequestLoggingFilter(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        String requestPath = exchange.getRequest().getPath().toString();
        HttpMethod requestMethod = exchange.getRequest().getMethod();
        String requestBody = exchange.getAttributeOrDefault(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR, "");

        exchange.getResponse().beforeCommit(() -> {
            HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
            int responseStatusCode = exchange.getResponse().getStatusCode().value();
            // You can log the response body if needed, but be cautious with large payloads
            // String responseBody = exchange.getAttributeOrDefault(ServerWebExchangeUtils.CACHED_RESPONSE_BODY_ATTR, "");

            RequestLogEntity requestLogEntity = RequestLogEntity.builder()
                    .createdAt(LocalDateTime.now())
                    .requestPath(requestPath)
                    .requestMethod(requestMethod.toString())
                    .requestBody(requestBody)
                    .requestHeaders(requestHeaders.toString())
                    .responseStatusCode(responseStatusCode)
                    .responseHeaders(responseHeaders.toString())
                    .build();

            requestLogRepository.save(requestLogEntity);
            return Mono.empty();
        });

        return chain.filter(exchange);
    }
}
