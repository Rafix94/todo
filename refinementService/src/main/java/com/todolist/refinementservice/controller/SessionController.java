package com.todolist.refinementservice.controller;

import com.todolist.refinementservice.dto.SessionResponseDTO;
import com.todolist.refinementservice.model.Session;
import com.todolist.refinementservice.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/{teamId}")
    public ResponseEntity<SessionResponseDTO> createOrGetSession(@PathVariable UUID teamId) {
        Session session = sessionService.createOrGetSession(teamId);
        return ResponseEntity.ok(new SessionResponseDTO(
                session.getId(),
                session.getTeamId(),
                session.isActive()
        ));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<SessionResponseDTO> getSession(@PathVariable UUID teamId) {
        SessionResponseDTO sessionResponseDTO = sessionService.getSession(teamId)
                .map(session -> new SessionResponseDTO(session.getId(), session.getTeamId(), session.isActive()))
                .orElse(new SessionResponseDTO(null, teamId, false));
        return ResponseEntity.ok(sessionResponseDTO);
    }
}