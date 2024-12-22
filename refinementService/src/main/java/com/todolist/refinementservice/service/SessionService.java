package com.todolist.refinementservice.service;

import com.todolist.refinementservice.SessionRepository;
import com.todolist.refinementservice.model.Participant;
import com.todolist.refinementservice.model.Session;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {
    private SessionRepository sessionRepository;

    @Transactional
    public Session createOrGetSession(UUID teamId) {
        return sessionRepository.findByTeamId(teamId)
                .orElseGet(() -> sessionRepository.save(Session.builder().teamId(teamId).active(true).build()));
    }

    public Optional<Session> getSession(UUID teamId) {
        return sessionRepository.findByTeamId(teamId);
    }

    @Transactional
    public Session joinSession(UUID teamId, UUID userId) {
        return sessionRepository.findByTeamId(teamId)
                .map(session -> {
                    session.getParticipants().add(
                            Participant.builder()
                                    .userId(userId)
                                    .session(session)
                                    .build());
                    return sessionRepository.save(session);
                }).orElseThrow(RuntimeException::new); //todo add exception handling
    }
}
