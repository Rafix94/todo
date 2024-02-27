package com.recruitment.useragent.repository;

import com.recruitment.useragent.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    Page<Task> findByCustomerId(Long userId, Pageable pageable);
}
