package com.todolist.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(name = "ErrorDto", description = "Object representing an error response")
public record ErrorDto(
        @Schema(description = "The path that caused the error", example = "/user/register", required = true)
        String invokedPath,

        @Schema(description = "The HTTP status code associated with the error", example = "400", required = true)
        HttpStatus httpStatus,

        @Schema(description = "The error message", example = "Invalid input", required = true)
        String message,

        @Schema(description = "The timestamp when the error occurred", example = "2024-03-03T12:34:56", required = true)
        LocalDateTime timestamp
) {
}