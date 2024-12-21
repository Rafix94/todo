package com.todolist.refinementservice.dto;

public record SessionMessageDTO (
        String teamId,
        String sessionId,
        String status,
        String currentTask
)
{}
