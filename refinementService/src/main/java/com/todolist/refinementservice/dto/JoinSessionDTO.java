package com.todolist.refinementservice.dto;

import java.util.UUID;

public record JoinSessionDTO (
        UUID userId,
        UUID teamId
)
{}
