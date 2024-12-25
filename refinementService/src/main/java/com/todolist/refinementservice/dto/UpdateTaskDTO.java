package com.todolist.refinementservice.dto;

import java.util.UUID;

public record UpdateTaskDTO(
        String title,
        String description
)
{}
