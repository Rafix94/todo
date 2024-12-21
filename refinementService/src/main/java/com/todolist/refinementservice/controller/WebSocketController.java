package com.todolist.refinementservice.controller;

import com.todolist.refinementservice.dto.SessionMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class WebSocketController {

    private final Map<String, String> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, String> currentTasks = new ConcurrentHashMap<>();

    @MessageMapping("/session/create")
    @SendTo("/topic/session-updates")
    public SessionMessageDTO createSession(SessionMessageDTO message) {
        activeSessions.put(message.teamId(), message.sessionId());
        return new SessionMessageDTO(
                message.teamId(),
                message.sessionId(),
                "CREATED",
                null
        );
    }

    @MessageMapping("/session/join")
    @SendTo("/topic/session-updates")
    public SessionMessageDTO joinSession(SessionMessageDTO message) {
        String sessionId = activeSessions.get(message.teamId());
        return new SessionMessageDTO(
                message.teamId(),
                sessionId,
                "JOINED",
                currentTasks.getOrDefault(message.teamId(), null)
        );
    }

    @MessageMapping("/session/task/update")
    @SendTo("/topic/session-updates")
    public SessionMessageDTO updateTask(SessionMessageDTO message) {
        currentTasks.put(message.teamId(), message.currentTask());
        return new SessionMessageDTO(
                message.teamId(),
                activeSessions.get(message.teamId()),
                "TASK_UPDATED",
                message.currentTask()
        );
    }

    @MessageMapping("/session/end")
    @SendTo("/topic/session-updates")
    public SessionMessageDTO endSession(SessionMessageDTO message) {
        return new SessionMessageDTO(
                message.teamId(),
                message.sessionId(),
                "ENDED",
                null
        );
    }
}