package com.todolist.refinementservice;

import com.todolist.refinementservice.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByTeamId(UUID teamId);
}
