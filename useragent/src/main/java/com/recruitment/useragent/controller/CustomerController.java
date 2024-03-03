package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.UserRegistrationDto;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.service.KeycloakUserService;
import com.recruitment.useragent.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User Registration", description = "Operations related to user registration")
public class CustomerController {

    private final UserService userService;
    private final KeycloakUserService keycloakUserService;

    @Autowired
    public CustomerController(UserService userService, KeycloakUserService keycloakUserService) {
        this.userService = userService;
        this.keycloakUserService = keycloakUserService;
    }

    @Operation(
            summary = "Register User",
            description = "Endpoint for registering a new user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<Customer> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        // Register user in Keycloak
        keycloakUserService.registerUser(userRegistrationDto.getUsername(),
                userRegistrationDto.getEmail(), userRegistrationDto.getPassword());

        // Save user in application database
        Customer customer = new Customer();
        customer.setUsername(userRegistrationDto.getUsername());
        customer.setEmail(userRegistrationDto.getEmail());
        userService.createCustomer(customer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customer);
    }
}
