package com.todolist.refinementservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@RestController
@RequestMapping("/ws-stats")
public class WebSocketStatsController {

    private final WebSocketMessageBrokerStats stats;

    public WebSocketStatsController(WebSocketMessageBrokerStats stats) {
        this.stats = stats;
    }

    @GetMapping
    public WebSocketMessageBrokerStats getStats() {
        return stats;
    }
}