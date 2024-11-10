package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "TaskDto",
        description = "Object representing a task to be created"
)
public record UpdateTaskDto(
        String title,
        String description
) {}


