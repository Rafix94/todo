package com.todolist.taskmanager.dto;

public record FileDto (
        String fileName,
        String url,
        Long size,
        String uploadDate
) {}
