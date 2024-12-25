package com.todolist.refinementservice.repository;

import com.todolist.refinementservice.model.Participant;
import com.todolist.refinementservice.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByUserIdAndSession(UUID userId, Session session);
}
