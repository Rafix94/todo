package com.recruitment.useragent.repository;

import com.recruitment.useragent.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    Page<Task> findByCustomerId(Long customerId, Pageable pageable);
    @Query("SELECT t FROM Task t WHERE t.customer.id = :customerId AND (LOWER(t.title) LIKE %:searchQuery% OR LOWER(t.description) LIKE %:searchQuery%)")
    Page<Task> findByCustomerIdAndSearchQuery(Long customerId, String searchQuery, Pageable pageable);
}
