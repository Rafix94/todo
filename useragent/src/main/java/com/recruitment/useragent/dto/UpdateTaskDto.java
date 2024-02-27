package com.recruitment.useragent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTaskDto {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}
