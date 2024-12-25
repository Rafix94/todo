package com.todolist.refinementservice.service;

import com.todolist.refinementservice.repository.ParticipantRepository;
import com.todolist.refinementservice.repository.SessionRepository;
import com.todolist.refinementservice.dto.ParticipantDTO;
import com.todolist.refinementservice.dto.SelectedTaskDTO;
import com.todolist.refinementservice.dto.SessionParticipantsDTO;
import com.todolist.refinementservice.dto.VotingStatusDTO;
import com.todolist.refinementservice.model.Participant;
import com.todolist.refinementservice.model.Session;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final KeycloakUserService keycloakUserService;
    private final ParticipantRepository participantRepository;

    @Transactional
    public Session createOrGetSession(UUID teamId) {
        return sessionRepository.findByTeamId(teamId)
                .orElseGet(() -> sessionRepository.save(Session.builder().teamId(teamId).active(true).build()));
    }

    public Optional<Session> getSession(UUID teamId) {
        return sessionRepository.findByTeamId(teamId);
    }

    @Transactional
    public SessionParticipantsDTO joinSession(UUID teamId, UUID userId) {
        Session session = sessionRepository.findByTeamId(teamId)
                .map(s -> {
                    s.getParticipants().add(
                            Participant.builder()
                                    .userId(userId)
                                    .session(s)
                                    .build());
                    return sessionRepository.save(s);
                }).orElseThrow(RuntimeException::new);

        return getSessionParticipantsDTO(session);
    }


    @Transactional
    public SessionParticipantsDTO leaveSession(UUID teamId, UUID userId) {
        Session session = sessionRepository.findByTeamId(teamId)
                .map(s -> {
                    s.getParticipants().removeIf(participant -> participant.getUserId().equals(userId));
                    return sessionRepository.save(s);
                }).orElseThrow(RuntimeException::new);

        return getSessionParticipantsDTO(session);
    }

    @Transactional
    public VotingStatusDTO startVoting(UUID teamId) {
        sessionRepository.findByTeamId(teamId)
                .map(s -> {
                    s.setActive(true);
                    return sessionRepository.save(s);
                }).orElseThrow(RuntimeException::new);

        return new VotingStatusDTO(teamId, true, null);
    }

    @Transactional
    public VotingStatusDTO stopVoting(UUID teamId) {
        sessionRepository.findByTeamId(teamId)
                .map(s -> {
                    s.setActive(false);
                    return sessionRepository.save(s);
                }).orElseThrow(RuntimeException::new);

        return new VotingStatusDTO(teamId, false, null);
    }

    public SelectedTaskDTO changeTask(SelectedTaskDTO selectedTaskDTO) {
        return new SelectedTaskDTO(selectedTaskDTO.taskTitle(), selectedTaskDTO.taskDescription());
    }


    @NotNull
    private SessionParticipantsDTO getSessionParticipantsDTO(Session session) {
        return new SessionParticipantsDTO(
                session.getParticipants().stream()
                        .map(participant -> {
                            String firstName = "Undefined";
                            String lastName = "Undefined";
                            String mail = "Undefined";

                            Optional<UserRepresentation> userRepresentationOptional = keycloakUserService.getUserById(String.valueOf(participant.getUserId()));
                            if (userRepresentationOptional.isPresent()) {
                                UserRepresentation userRepresentation = userRepresentationOptional.get();
                                firstName = userRepresentation.getFirstName() != null ? userRepresentation.getFirstName() : "Undefined";
                                lastName = userRepresentation.getLastName() != null ? userRepresentation.getLastName() : "Undefined";
                                mail = userRepresentation.getEmail() != null ? userRepresentation.getEmail() : "Undefined";
                            }

                            return new ParticipantDTO(participant.getUserId(), mail, firstName, lastName);
                        })
                        .toList()
        );
    }
}
