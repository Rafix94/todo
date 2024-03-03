package com.recruitment.useragent.controller;

import com.recruitment.useragent.dto.UserRegistrationDto;
import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.service.KeycloakUserService;
import com.recruitment.useragent.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class CustomerController {

    UserService userService;
    private KeycloakUserService keycloakUserService;

    @Autowired
    public CustomerController(UserService userService, KeycloakUserService keycloakUserService) {
        this.userService = userService;
        this.keycloakUserService = keycloakUserService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto UserRegistrationDto) {
        // Register user in Keycloak
        keycloakUserService.registerUser(UserRegistrationDto.getUsername(),
                UserRegistrationDto.getEmail(), UserRegistrationDto.getPassword());

        // Save user in application database
        Customer customer = new Customer();

        customer.setUsername(UserRegistrationDto.getUsername());
        customer.setEmail(UserRegistrationDto.getEmail());
        userService.createCustomer(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}