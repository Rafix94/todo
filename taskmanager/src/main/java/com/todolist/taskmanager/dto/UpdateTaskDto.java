package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateTaskDto", description = "Object representing the data required to update an existing task")
public record UpdateTaskDto(
        @Schema(description = "The updated title of the task", example = "Finalize the project design")
        String title,

        @Schema(description = "The updated description of the task", example = "Complete all design specifications and mockups")
        String description
) {}