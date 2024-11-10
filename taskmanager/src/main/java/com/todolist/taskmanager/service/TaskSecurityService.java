package com.todolist.taskmanager.service;

import com.todolist.taskmanager.dto.TaskDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TaskSecurityService {
    private final TaskService taskService;

    public boolean isOwner(long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // Assuming the user's UUID is stored as the username in Keycloak

        TaskDto task = taskService.getTaskById(taskId);
        return task.createdBy().toString().equals(currentUserId);
    }
}
