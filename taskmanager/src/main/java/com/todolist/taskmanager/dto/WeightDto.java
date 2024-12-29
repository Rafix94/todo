package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for task weight")
public record WeightDto(
        @Schema(description = "Weight of the task", example = "5", required = true)
        Integer weight
) {}