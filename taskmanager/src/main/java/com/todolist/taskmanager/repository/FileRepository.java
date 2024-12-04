package com.todolist.taskmanager.repository;

import com.todolist.taskmanager.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
