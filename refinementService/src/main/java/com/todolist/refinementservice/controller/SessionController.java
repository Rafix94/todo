package com.todolist.refinementservice.controller;

import com.todolist.refinementservice.dto.VotingStatusDTO;
import com.todolist.refinementservice.model.Session;
import com.todolist.refinementservice.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/{teamId}")
    public ResponseEntity<VotingStatusDTO> createOrGetSession(@PathVariable UUID teamId) {
        Session session = sessionService.createOrGetSession(teamId);
        return ResponseEntity.ok(new VotingStatusDTO(
                session.getTeamId(),
                session.isActive(),
                null
        ));
    }

//    @GetMapping("/{teamId}")
//    public ResponseEntity<VotingStatusDTO> getSession(@PathVariable UUID teamId) {
//        VotingStatusDTO sessionResponseDTO = sessionService.getSession(teamId)
//                .map(session -> new VotingStatusDTO(session.getTeamId(), session.isActive(), session.getVotes()))
//                .orElse(new VotingStatusDTO(teamId, false, null));
//        return ResponseEntity.ok(sessionResponseDTO);
//    }
}