package com.todolist.refinementservice.service;

import com.todolist.refinementservice.SessionRepository;
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
                .orElseGet(() -> sessionRepository.save(Session.builder().teamId(teamId).build()));
    }

    public Optional<Session> getSession(UUID teamId) {
        return sessionRepository.findByTeamId(teamId);
    }
}
