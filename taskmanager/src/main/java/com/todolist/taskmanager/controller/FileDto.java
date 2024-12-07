package com.todolist.taskmanager.controller;

public record FileDto(
        Long id,
        String name,
        Long size,
        String url
) {}
