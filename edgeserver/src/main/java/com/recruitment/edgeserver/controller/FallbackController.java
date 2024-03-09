package com.recruitment.edgeserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
    @RequestMapping("/support")
    public Mono<String> contact() {
        return Mono.just("An error occurred. Please try again later in 10 minutes");
    }
}
