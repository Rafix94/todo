package com.todolist.taskmanager.dto;

import com.todolist.taskmanager.controller.FileDto;
import java.time.LocalDateTime;

public record CommentDto (
        Long id,
        String comment,
        LocalDateTime createdAt,
        FileDto fileDto
)
{}
