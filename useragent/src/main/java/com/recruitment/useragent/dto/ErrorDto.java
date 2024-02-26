package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(
        name = "ErrorDto",
        description = "In case of error"
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDto {
    @Schema(description = "invoked path")
    private  String invokedPath;

    @Schema(description = "error code")
    private HttpStatus httpStatus;

    @Schema(description = "message")
    private  String message;

    @Schema(description = "error occurrence date")
    private LocalDateTime timestamp;
}
