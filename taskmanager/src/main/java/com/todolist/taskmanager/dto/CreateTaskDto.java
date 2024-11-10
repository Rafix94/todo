package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "CreateTaskDto", description = "Object representing the data required to create a new task")
public record CreateTaskDto(
        @Schema(description = "Title of the task", example = "Complete the project report", required = true)
        @NotNull String title,

        @Schema(description = "Description of the task", example = "A detailed description of the task objectives", required = true)
        @NotNull String description,

        @Schema(description = "ID of the team associated with this task", example = "c56a4180-65aa-42ec-a945-5fd21dec0538", required = true)
        @NotNull UUID teamId
) {}