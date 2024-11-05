package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(
        name = "TaskDto",
        description = "Object representing a task to be created"
)
public record CreateTaskDto(
        @NotNull String title,
        @NotNull String description,
        @NotNull UUID teamId
) {}
