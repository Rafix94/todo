package com.todolist.refinementservice.mapper;

import com.todolist.refinementservice.dto.UserDataDTO;
import com.todolist.refinementservice.dto.UserVoteStateDTO;
import com.todolist.refinementservice.service.KeycloakUserService;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SessionStateMapper {

    KeycloakUserService keycloakUserService;

    public Map<UserDataDTO, UserVoteStateDTO> getParticipants(Map<UUID, UserVoteStateDTO> participants) {
        return participants.entrySet().stream().collect(Collectors.toMap(
                entry -> {
                    Optional<UserRepresentation> user = keycloakUserService.getUserById(String.valueOf(entry.getKey()));
                    String firstName = "Undefined";
                    String lastName = "Undefined";
                    if (user.isPresent()) {
                        UserRepresentation userRepresentation = user.get();
                        firstName = userRepresentation.getFirstName() != null ? userRepresentation.getFirstName() : firstName;
                        lastName = userRepresentation.getLastName() != null ? userRepresentation.getLastName() : lastName;
                    }
                    return new UserDataDTO(entry.getKey(), firstName, lastName);
                },
                entry -> new UserVoteStateDTO(entry.getValue().voted(), entry.getValue().score())
        ));
    }

}
