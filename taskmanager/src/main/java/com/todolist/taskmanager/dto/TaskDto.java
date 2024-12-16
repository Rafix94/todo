package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "TaskDto", description = "Object representing a task")
public record TaskDto(
        @Schema(description = "The unique identifier of the task", example = "1", required = true)
        Long id,

        @Schema(description = "The title of the task", example = "Complete project documentation", required = true)
        String title,

        @Schema(description = "The description of the task", example = "Write and finalize the project documentation", required = true)
        String description,

        @Schema(description = "The UUID of the user who created the task", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
        UUID createdBy,

        @Schema(description = "The UUID of the user assigned to the task", example = "789e4567-e89b-12d3-a456-426614174001")
        UUID assignedTo,

        @Schema(description = "Email of the task creator", example = "creator@example.com", required = true)
        String creatorEmail,

        @Schema(description = "Email of the user assigned to the task", example = "assignee@example.com")
        String assigneeEmail,

        @Schema(description = "The UUID of the team associated with this task", example = "c56a4180-65aa-42ec-a945-5fd21dec0538", required = true)
        UUID teamId,

        List<CommentDto> commentDtos
) {}