package com.todolist.refinementservice.controller;
import com.todolist.refinementservice.dto.*;
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
        return sessionService.joinSession(joinSessionDTO.teamId(), joinSessionDTO.userId());
    }

    @MessageMapping("/session/leave")
    @SendTo("/topic/session/participants")
    public SessionParticipantsDTO leaveSession(@Payload JoinSessionDTO joinSessionDTO) {
        return sessionService.leaveSession(joinSessionDTO.teamId(), joinSessionDTO.userId());
    }

    @MessageMapping("/session/voting/start")
    @SendTo("/topic/voting/status")
    public VotingStatusDTO startVoting(@Payload VotingStatusDTO votingStatusDTO) {
        return sessionService.startVoting(votingStatusDTO.teamId());
    }

    @MessageMapping("/session/voting/stop")
    @SendTo("/topic/voting/status")
    public VotingStatusDTO stopVoting(@Payload VotingStatusDTO votingStatusDTO) {
        return sessionService.stopVoting(votingStatusDTO.teamId());
    }

    @MessageMapping("/session/task/update")
    @SendTo("/topic/task/update")
    public SelectedTaskDTO updateTask(@Payload SelectedTaskDTO selectedTaskDTO) {
        return sessionService.changeTask(selectedTaskDTO);
    }
}