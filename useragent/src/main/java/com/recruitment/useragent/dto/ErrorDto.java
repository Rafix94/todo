package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(name = "ErrorDto", description = "Object representing an error response")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDto {

    @Schema(description = "The path that caused the error", example = "/user/register")
    private String invokedPath;

    @Schema(description = "The HTTP status code associated with the error", example = "400")
    private HttpStatus httpStatus;

    @Schema(description = "The error message", example = "Invalid input")
    private String message;

    @Schema(description = "The timestamp when the error occurred", example = "2024-03-03T12:34:56")
    private LocalDateTime timestamp;
}