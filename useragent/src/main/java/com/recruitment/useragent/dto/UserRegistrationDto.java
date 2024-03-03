package com.recruitment.useragent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;

}
