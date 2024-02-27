package com.recruitment.useragent.controller;

import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllUsers() {
        List<Customer> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Customer> getUserById(@PathVariable Long userId) {
        Customer user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Customer> createUser(@RequestBody Customer user) {
        Customer createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
