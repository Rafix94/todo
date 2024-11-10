package com.todolist.taskmanager.service;

import com.todolist.taskmanager.dto.TaskDto;
import lombok.AllArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TeamSecurityService {

    private final TaskService taskService;


    public boolean userBelongsToTeam(UUID teamId) {
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) authentication.getPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();

        Set<String> userGroups = (Set<String>) accessToken.getOtherClaims().get("groups");

        return userGroups != null && userGroups.contains("/" + teamId);
    }

    public boolean taskBelongsToUsersTeam(long taskId) {
        TaskDto taskDto = taskService.getTaskById(taskId);

        return userBelongsToTeam(taskDto.teamId());
    }
}