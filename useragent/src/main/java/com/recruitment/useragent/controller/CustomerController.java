package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.UserRegistrationDto;
import com.recruitment.useragent.service.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tag(name = "User Registration", description = "Operations related to user registration")
public class CustomerController {

    private final KeycloakUserService keycloakUserService;

    @Operation(
            summary = "Register User",
            description = "Endpoint for registering a new user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        keycloakUserService.registerUser(
                userRegistrationDto.username(),
                userRegistrationDto.firstName(),
                userRegistrationDto.lastName(),
                userRegistrationDto.email(),
                userRegistrationDto.password(),
                List.of("USER"));

        return ResponseEntity
                .status(HttpStatus.CREATED).build();
    }
}
