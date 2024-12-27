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
    public void joinSession(@Payload JoinSessionDTO joinSessionDTO) {
        sessionService.joinSession(joinSessionDTO.teamId(), joinSessionDTO.userId());
    }

    @MessageMapping("/session/leave")
    public void leaveSession(@Payload JoinSessionDTO joinSessionDTO) {
        sessionService.leaveSession(joinSessionDTO.teamId(), joinSessionDTO.userId());
    }

    @MessageMapping("/session/task/update")
    public void updateTask(@Payload SelectedTaskDTO selectedTaskDTO) {
        sessionService.changeTask(selectedTaskDTO);
    }
}