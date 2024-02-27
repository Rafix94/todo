package com.recruitment.useragent.service;

import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.exception.NotFoundException;
import com.recruitment.useragent.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Customer> getAllUsers() {
        return userRepository.findAll();
    }

    public Customer getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", "id", String.valueOf(userId)));
    }

    public Customer createUser(Customer user) {
        return userRepository.save(user);
    }
}