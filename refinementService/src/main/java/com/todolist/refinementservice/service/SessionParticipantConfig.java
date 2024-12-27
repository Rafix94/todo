package com.todolist.refinementservice.service;

import com.todolist.refinementservice.config.TopicConfig;
import com.todolist.refinementservice.dto.SelectedTaskDTO;
import com.todolist.refinementservice.dto.UserPresenceActionDTO;
import com.todolist.refinementservice.dto.VoteSubmissionDTO;
import com.todolist.refinementservice.dto.VotingStatusChangeDTO;
import com.todolist.refinementservice.mapper.SessionStateMapper;
import com.todolist.refinementservice.model.SessionState;
import com.todolist.refinementservice.model.Task;
import com.todolist.refinementservice.model.UserVoteState;
import com.todolist.refinementservice.model.VotingState;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Configuration
@AllArgsConstructor
public class SessionParticipantConfig {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionStateMapper sessionStateMapper;

    @Bean
    public KStream<UUID, UserPresenceActionDTO> userPresenceStream(StreamsBuilder streamsBuilder) {
        try (JsonSerde<UserPresenceActionDTO> userPresenceActionDTOJsonSerde = new JsonSerde<>(UserPresenceActionDTO.class);
             JsonSerde<SessionState> sessionStateJsonSerde = new JsonSerde<>(SessionState.class);
             JsonSerde<SelectedTaskDTO> selectedTaskDTOJsonSerde = new JsonSerde<>(SelectedTaskDTO.class);
             JsonSerde<VoteSubmissionDTO> voteSubmissionDTOJsonSerde = new JsonSerde<>(VoteSubmissionDTO.class);
             JsonSerde<VotingStatusChangeDTO> votingStatusChangeDTOJsonSerde = new JsonSerde<>(VotingStatusChangeDTO.class)) {

            KStream<UUID, UserPresenceActionDTO> userPresenceActionStream = streamsBuilder.stream(
                    TopicConfig.USER_PRESENCE_TOPIC,
                    Consumed.with(Serdes.String(), userPresenceActionDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));

            KStream<UUID, SelectedTaskDTO> selectedTaskStream = streamsBuilder.stream(
                    TopicConfig.TASK_SELECTION_TOPIC,
                    Consumed.with(Serdes.String(), selectedTaskDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));

            KStream<UUID, VoteSubmissionDTO> voteStream = streamsBuilder.stream(
                    TopicConfig.VOTES_TOPIC,
                    Consumed.with(Serdes.String(), voteSubmissionDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));

            KStream<UUID, VotingStatusChangeDTO> votingStatusStream = streamsBuilder.stream(
                    TopicConfig.VOTING_STATUS_TOPIC,
                    Consumed.with(Serdes.String(), votingStatusChangeDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));

            Materialized<UUID, SessionState, KeyValueStore<Bytes, byte[]>> sessionStore =
                    Materialized.<UUID, SessionState, KeyValueStore<Bytes, byte[]>>as("user-presence-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(sessionStateJsonSerde);

            KTable<UUID, SessionState> userPresenceActionKTable = userPresenceActionStream.groupByKey(Grouped.with(Serdes.UUID(), userPresenceActionDTOJsonSerde))
                    .aggregate(
                            SessionState::newSession,
                            (teamId, userPresenceActionDTO, sessionState) -> {
                                Map<UUID, UserVoteState> updatedParticipantsVotes = new HashMap<>(sessionState.participantsVotes());
                                UUID newAdminId = sessionState.adminId();

                                switch (userPresenceActionDTO.presenceAction()) {
                                    case JOIN -> {
                                        updatedParticipantsVotes.put(
                                                userPresenceActionDTO.userId(),
                                                new UserVoteState(false, null));
                                        if (updatedParticipantsVotes.size() == 1) {
                                            newAdminId = userPresenceActionDTO.userId();
                                        }
                                    }
                                    case LEAVE -> {
                                        updatedParticipantsVotes.remove(userPresenceActionDTO.userId());
                                        if (updatedParticipantsVotes.isEmpty()) {
                                            return null;
                                        }
                                        if (Objects.equals(newAdminId, userPresenceActionDTO.userId())) {
                                            newAdminId = updatedParticipantsVotes.keySet().iterator().next();
                                        }
                                    }
                                }

                                return new SessionState(
                                        updatedParticipantsVotes,
                                        newAdminId,
                                        sessionState.votingState(),
                                        sessionState.selectedTask()
                                );
                            },
                            sessionStore
                    );

            KTable<UUID, SelectedTaskDTO> selectedTaskKTable = selectedTaskStream.toTable(
                    Materialized.<UUID, SelectedTaskDTO, KeyValueStore<Bytes, byte[]>>as("selected-task-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(selectedTaskDTOJsonSerde)
            );

            KTable<UUID, VoteSubmissionDTO> voteKTable = voteStream.toTable(
                    Materialized.<UUID, VoteSubmissionDTO, KeyValueStore<Bytes, byte[]>>as("vote-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(voteSubmissionDTOJsonSerde)
            );

            KTable<UUID, VotingStatusChangeDTO> votingStatusKTable = votingStatusStream.toTable(
                    Materialized.<UUID, VotingStatusChangeDTO, KeyValueStore<Bytes, byte[]>>as("voting-status-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(votingStatusChangeDTOJsonSerde)
            );

            KTable<UUID, SessionState> sessionStateTable = userPresenceActionKTable.leftJoin(selectedTaskKTable,
                    (sessionState, selectedTaskDTO) -> {
                        if (selectedTaskDTO != null) {
                            return new SessionState(
                                    sessionState.participantsVotes(),
                                    sessionState.adminId(),
                                    sessionState.votingState(),
                                    new Task(selectedTaskDTO.taskTitle(), selectedTaskDTO.taskDescription())
                            );
                        }
                        return sessionState;
                    },
                    Materialized.<UUID, SessionState, KeyValueStore<Bytes, byte[]>>as("session-state-with-task-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(sessionStateJsonSerde)
            ).leftJoin(voteKTable,
                    (sessionState, voteSubmissionDTO) -> {
                        if (voteSubmissionDTO != null) {
                            var updatedVotes = new HashMap<>(sessionState.participantsVotes());
                            updatedVotes.put(
                                    voteSubmissionDTO.userId(),
                                    new UserVoteState(true, voteSubmissionDTO.vote())
                            );
                            return new SessionState(
                                    updatedVotes,
                                    sessionState.adminId(),
                                    sessionState.votingState(),
                                    sessionState.selectedTask()
                            );
                        }
                        return sessionState;
                    },
                    Materialized.<UUID, SessionState, KeyValueStore<Bytes, byte[]>>as("session-state-store-with-vote-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(sessionStateJsonSerde)
            ).leftJoin(votingStatusKTable,
                    (sessionState, votingStatusChangeDTO) -> {
                        if (votingStatusChangeDTO != null) {
                            var updatedVotes = new HashMap<>(sessionState.participantsVotes());
                            if (votingStatusChangeDTO.votingState() == VotingState.IDLE) {
                                updatedVotes.replaceAll((key, value) -> new UserVoteState(false, null));
                            }
                            return new SessionState(
                                    updatedVotes,
                                    sessionState.adminId(),
                                    votingStatusChangeDTO.votingState(),
                                    sessionState.selectedTask()
                            );
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

            return userPresenceActionStream;
        }
    }
}
