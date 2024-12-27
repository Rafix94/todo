package com.todolist.refinementservice.service;

import com.todolist.refinementservice.config.TopicConfig;
import com.todolist.refinementservice.dto.SelectedTaskDTO;
import com.todolist.refinementservice.dto.UserPresenceActionDTO;
import com.todolist.refinementservice.mapper.SessionStateMapper;
import com.todolist.refinementservice.model.SessionState;
import com.todolist.refinementservice.model.Task;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

@Configuration
@AllArgsConstructor
public class SessionParticipantConfig {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionStateMapper sessionStateMapper;

    @Bean
    public KStream<UUID, UserPresenceActionDTO> userPresenceStream(StreamsBuilder streamsBuilder) {
        JsonSerde<UserPresenceActionDTO> userPresenceActionDTOJsonSerde = new JsonSerde<>(UserPresenceActionDTO.class);
        JsonSerde<SessionState> sessionStateJsonSerde = new JsonSerde<>(SessionState.class);
        JsonSerde<SelectedTaskDTO> selectedTaskDTOJsonSerde = new JsonSerde<>(SelectedTaskDTO.class);

        KStream<String, UserPresenceActionDTO> userPresenceActionStream = streamsBuilder.stream(
                TopicConfig.USER_PRESENCE_TOPIC,
                Consumed.with(Serdes.String(), userPresenceActionDTOJsonSerde)
        );

        KStream<String, SelectedTaskDTO> selectedTaskStream = streamsBuilder.stream(
                TopicConfig.TASK_SELECTION_TOPIC,
                Consumed.with(Serdes.String(), selectedTaskDTOJsonSerde)
        );

        KStream<UUID, UserPresenceActionDTO> rekeyedPresenceStream = userPresenceActionStream.selectKey((key, value) -> {
            try {
                return UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid UUID key: " + key, e);
            }
        });

        KStream<UUID, SelectedTaskDTO> rekeyedTaskStream = selectedTaskStream.selectKey((key, value) -> {
            try {
                return UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid UUID key: " + key, e);
            }
        });

        Materialized<UUID, SessionState, KeyValueStore<Bytes, byte[]>> sessionStore =
                Materialized.<UUID, SessionState, KeyValueStore<Bytes, byte[]>>as("user-presence-store")
                        .withKeySerde(Serdes.UUID())
                        .withValueSerde(sessionStateJsonSerde);

        KTable<UUID, SessionState> userPresenceActionKTable = rekeyedPresenceStream.groupByKey(Grouped.with(Serdes.UUID(), userPresenceActionDTOJsonSerde))
                .aggregate(
                        SessionState::new,
                        (teamId, userPresenceActionDTO, sessionState) -> {
                            switch (userPresenceActionDTO.presenceAction()) {
                                case JOIN -> {
                                    sessionState.getParticipantsVotes().put(userPresenceActionDTO.userId(), null);
                                    if (sessionState.getParticipantsVotes().size() == 1) {
                                        sessionState.setAdminId(userPresenceActionDTO.userId());
                                    }
                                }
                                case LEAVE -> {
                                    sessionState.getParticipantsVotes().remove(userPresenceActionDTO.userId());
                                    if (sessionState.getParticipantsVotes().isEmpty()) {
                                        return null;
                                    }
                                    if (sessionState.getAdminId().equals(userPresenceActionDTO.userId())) {
                                        sessionState.setAdminId(
                                                sessionState.getParticipantsVotes().keySet().iterator().next());
                                    }
                                }
                            }
                            return sessionState;
                        },
                        sessionStore
                );

        KTable<UUID, SelectedTaskDTO> selectedTaskKTable = rekeyedTaskStream.toTable(
                Materialized.<UUID, SelectedTaskDTO, KeyValueStore<Bytes, byte[]>>as("selected-task-store")
                        .withKeySerde(Serdes.UUID())
                        .withValueSerde(selectedTaskDTOJsonSerde)
        );

        KTable<UUID, SessionState> sessionStateTable = userPresenceActionKTable.leftJoin(selectedTaskKTable,
                (sessionState, selectedTaskDTO) -> {
                    if (selectedTaskDTO != null) {
                        sessionState.setSelectedTask(new Task(selectedTaskDTO.taskTitle(), selectedTaskDTO.taskDescription()));
                    }
                    return sessionState;
                },
                Materialized.<UUID, SessionState, KeyValueStore<Bytes, byte[]>>as("session-state-store")
                        .withKeySerde(Serdes.UUID())
                        .withValueSerde(sessionStateJsonSerde)
        );

        sessionStateTable.toStream().foreach((teamId, sessionState) -> {
            if (sessionState != null) {
                messagingTemplate.convertAndSend(
                        "/topic/session/" + teamId,
                        sessionStateMapper.sessionStateToSessionStateDTO(sessionState)
                );
            }
        });

        return rekeyedPresenceStream;
    }
}
