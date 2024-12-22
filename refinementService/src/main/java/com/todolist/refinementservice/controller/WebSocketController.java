package com.todolist.refinementservice.controller;
import com.todolist.refinementservice.dto.JoinSessionDTO;
import com.todolist.refinementservice.dto.ParticipantDTO;
import com.todolist.refinementservice.dto.SessionParticipantsDTO;
import com.todolist.refinementservice.model.Session;
import com.todolist.refinementservice.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebSocketController {

    private final SessionService sessionService;

    @MessageMapping("/session/join")
    @SendTo("/topic/session/participants")
    public SessionParticipantsDTO joinSession(@Payload JoinSessionDTO joinSessionDTO) {
        Session session = sessionService.joinSession(joinSessionDTO.teamId(), joinSessionDTO.userId());

        return new SessionParticipantsDTO(
                session.getParticipants().stream()
                        .map(participant -> new ParticipantDTO(participant.getUserId(), "tmp@example.com")) // Replace with actual email logic
                        .toList()
        );
    }

}