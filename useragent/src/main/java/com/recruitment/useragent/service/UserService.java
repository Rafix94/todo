package com.recruitment.useragent.service;

import com.recruitment.useragent.entity.Customer;
import com.recruitment.useragent.exception.NotFoundException;
import com.recruitment.useragent.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final CustomerRepository customerRepository;

    public UserService(CustomerRepository userRepository) {
        this.customerRepository = userRepository;
    }

    public List<Customer> getAllUsers() {
        return customerRepository.findAll();
    }

    public Customer getUserById(Long userId) {
        return customerRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", "id", String.valueOf(userId)));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
