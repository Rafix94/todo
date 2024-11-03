package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(
        name = "UserRegistrationDto",
        description = "Object representing user registration information"
)
public record UserRegistrationDto (
    @Schema(description = "The username of the user")
    String username,
    @Schema(description = "The email address of the user")
    String email,
    @Schema(description = "The password of the user")
    String password,
    @Schema(description = "The userName of the user")
    String firstName,
    @Schema(description = "The surname of the user")
    String lastName
    ) {}
