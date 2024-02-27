package com.recruitment.useragent.repository;

import com.recruitment.useragent.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Customer, Long> {
}