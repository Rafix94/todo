package com.todolist.refinementservice.dto;

import java.util.UUID;

public record SessionResponseDTO (
        Long sessionId,
        UUID teamId,
        boolean active
)
{}
