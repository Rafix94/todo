package com.todolist.taskmanager.service;

import com.todolist.taskmanager.model.Comment;
import com.todolist.taskmanager.model.Task;
import com.todolist.taskmanager.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    Comment createComment(String commentValue, Task task) {
        Comment comment = commentRepository.save(
                Comment.builder()
                        .comment(commentValue)
                        .task(task)
                        .build());
        return commentRepository.save(comment);

    }
}
