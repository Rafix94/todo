package com.todolist.taskmanager.controller;

public record FileDTO (
        String name,
        Long size,
        String url
) {}
