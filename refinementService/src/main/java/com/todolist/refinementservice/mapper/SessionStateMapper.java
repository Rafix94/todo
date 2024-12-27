package com.todolist.refinementservice.mapper;

import com.todolist.refinementservice.dto.SessionStateDTO;
import com.todolist.refinementservice.dto.UserDataDTO;
import com.todolist.refinementservice.dto.UserVoteStateDTO;
import com.todolist.refinementservice.model.SessionState;
import com.todolist.refinementservice.model.UserVoteState;
import com.todolist.refinementservice.service.KeycloakUserService;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SessionStateMapper {

    KeycloakUserService keycloakUserService;

    public SessionStateDTO sessionStateToSessionStateDTO(SessionState sessionState) {
        Map<UserDataDTO, UserVoteStateDTO> participantsVotes = sessionState.getParticipantsVotes().entrySet().stream()
                .collect(Collectors.toMap(
                        (entry) -> {
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
                        (entry) ->
                        {
                            UserVoteState userVoteState = entry.getValue();
                            return switch (sessionState.getVotingState()) {
                                case ACTIVE -> new UserVoteStateDTO(userVoteState.isVoted(), null);
                                case IDLE -> new UserVoteStateDTO(null, null);
                                case REVEALED ->
                                        new UserVoteStateDTO(userVoteState.isVoted(), userVoteState.getScore());
                            };
                        }
                ));

        return new SessionStateDTO(participantsVotes, sessionState.getAdminId(), sessionState.getVotingState(), sessionState.getSelectedTask());
    }

}
