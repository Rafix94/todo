package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(
        name = "UserRegistrationDto",
        description = "Object representing user registration information"
)
public class UserRegistrationDto {

    @Schema(description = "The username of the user")
    private String username;

    @Schema(description = "The email address of the user")
    private String email;

    @Schema(description = "The password of the user")
    private String password;

}
