package com.todolist.refinementservice.service;

import com.todolist.refinementservice.config.TopicConfig;
import com.todolist.refinementservice.dto.PresenceAction;
import com.todolist.refinementservice.dto.SelectedTaskDTO;
import com.todolist.refinementservice.dto.UserPresenceActionDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void joinSession(UUID teamId, UUID userId) {
        kafkaTemplate.send(TopicConfig.USER_PRESENCE_TOPIC,
                String.valueOf(teamId),
                new UserPresenceActionDTO(userId, teamId, PresenceAction.JOIN));
    }

    public void leaveSession(UUID teamId, UUID userId) {
        kafkaTemplate.send(TopicConfig.USER_PRESENCE_TOPIC,
                String.valueOf(teamId),
                new UserPresenceActionDTO(userId, teamId, PresenceAction.LEAVE));
    }

    public void changeTask(SelectedTaskDTO selectedTaskDTO) {
        kafkaTemplate.send(TopicConfig.TASK_SELECTION_TOPIC, String.valueOf(selectedTaskDTO.teamId()), selectedTaskDTO);
    }

}
