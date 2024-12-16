package com.todolist.taskmanager.repository;

import com.todolist.taskmanager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
