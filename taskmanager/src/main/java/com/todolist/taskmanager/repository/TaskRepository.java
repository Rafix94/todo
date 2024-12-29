package com.todolist.taskmanager.repository;

import com.todolist.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTeamId(UUID teamId, Pageable pageable);
    List<Task> findAllByTeamId(UUID teamId);
}
