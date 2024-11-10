package com.todolist.taskmanager.service;

import com.todolist.taskmanager.dto.TaskDto;
import com.todolist.taskmanager.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TeamSecurityService {

    private final TaskService taskService;
    private final KeycloakUserService keycloakUserService;


    public boolean userBelongsToTeam(UUID teamId) {
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        List<String> userGroups = (List<String>) authentication.getTokenAttributes().get("groups");

        List<GroupRepresentation> groups = keycloakUserService.getGroups();

        GroupRepresentation groupRepresentation = groups.stream()
                .filter(groupRepresentation1 -> groupRepresentation1.getId().equals(teamId.toString()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Team", "id", String.valueOf(teamId.toString())));


        return userGroups.contains("/" + groupRepresentation.getName());
    }

    public boolean taskBelongsToUsersTeam(long taskId) {
        TaskDto taskDto = taskService.getTaskById(taskId);

        return userBelongsToTeam(taskDto.teamId());
    }
}