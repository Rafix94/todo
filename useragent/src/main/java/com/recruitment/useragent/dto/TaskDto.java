package com.recruitment.useragent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Data
@Builder
@Schema(
        name = "TaskDto",
        description = "Object representing a task"
)
public class TaskDto {

    @Schema(description = "The unique ID of the task")
    private Long id;

    @NotBlank(message = "Title is required")
    @Schema(description = "The title of the task", example = "Code")
    private String title;

    @Schema(description = "The description of the task", example = "Todo list app")
    private String description;

    @NotNull(message = "Due date is required")
    @Schema(description = "The due date of the task", example = "2024-03-01")
    private LocalDate dueDate;

    @NotBlank(message = "Priority is required")
    @Schema(description = "The priority of the task", example = "HIGH")
    private String priority;

    @NotBlank(message = "Status is required")
    @Schema(description = "The status of the task", example = "HIGH")
    private String status;

    @NotBlank(message = "Category is required")
    @Schema(description = "The category of the task", example = "Work")
    private String category;

}
