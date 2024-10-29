package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "TaskDto",
        description = "Object representing a task"
)
public record TaskDto(
        Long id,
        String title,
        String description,
        UUID createdBy,
        UUID assignedTo
) {}
