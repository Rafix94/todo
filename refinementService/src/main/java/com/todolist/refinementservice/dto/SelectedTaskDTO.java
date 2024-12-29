package com.todolist.refinementservice.dto;

import java.util.UUID;

public record SelectedTaskDTO(
        String taskTitle,
        String taskDescription,
        UUID teamId
)
{}
