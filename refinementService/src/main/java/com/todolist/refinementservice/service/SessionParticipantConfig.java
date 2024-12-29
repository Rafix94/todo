package com.todolist.refinementservice.service;

import com.todolist.refinementservice.config.TopicConfig;
import com.todolist.refinementservice.dto.*;
import com.todolist.refinementservice.mapper.SessionStateMapper;
import com.todolist.refinementservice.dto.UserVoteStateDTO;
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

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
public class SessionParticipantConfig {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionStateMapper sessionStateMapper;

    @Bean
    public KStream<UUID, UserPresenceActionDTO> userPresenceStream(StreamsBuilder streamsBuilder) {
        try (JsonSerde<UserPresenceActionDTO> userPresenceActionDTOJsonSerde = new JsonSerde<>(UserPresenceActionDTO.class);
             JsonSerde<VoteSubmissionDTO> voteSubmissionDTOJsonSerde = new JsonSerde<>(VoteSubmissionDTO.class);
             JsonSerde<VotingStatusChangeDTO> votingStatusChangeDTOJsonSerde = new JsonSerde<>(VotingStatusChangeDTO.class)) {

            KStream<UUID, UserPresenceActionDTO> userPresenceActionStream = streamsBuilder.stream(
                    TopicConfig.USER_PRESENCE_TOPIC,
                    Consumed.with(Serdes.String(), userPresenceActionDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));

            KStream<UUID, VoteSubmissionDTO> voteStream = streamsBuilder.stream(
                    TopicConfig.VOTES_TOPIC,
                    Consumed.with(Serdes.String(), voteSubmissionDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));

            KTable<UUID, Map<UUID, UserVoteStateDTO>> userPresenceTable = userPresenceActionStream
                    .groupByKey(Grouped.with(Serdes.UUID(), userPresenceActionDTOJsonSerde))
                    .aggregate(
                            HashMap::new,
                            (teamId, userPresenceActionDTO, userVoteStateMap) -> {
                                var updatedUserPresenceStateMap = new HashMap<>(userVoteStateMap);

                                switch (userPresenceActionDTO.presenceAction()) {
                                    case JOIN -> updatedUserPresenceStateMap.put(
                                            userPresenceActionDTO.userId(),
                                            new UserVoteStateDTO(false, null));
                                    case LEAVE -> {
                                        updatedUserPresenceStateMap.remove(userPresenceActionDTO.userId());
                                        if (updatedUserPresenceStateMap.isEmpty()) {
                                            return null;
                                        }
                                    }
                                }
                                return updatedUserPresenceStateMap;
                            },
                            Materialized.<UUID, Map<UUID, UserVoteStateDTO>, KeyValueStore<Bytes, byte[]>>as("user-presence-store")
                                    .withKeySerde(Serdes.UUID())
                                    .withValueSerde(MapSerde.userVoteStateMapSerde())
                    );

            KTable<UUID, UUID> adminTable = userPresenceTable.mapValues((teamId, participants) -> {
                if (participants == null || participants.isEmpty()) {
                    return null;
                }

                return participants.keySet().iterator().next();
            });

            adminTable.toStream().foreach((teamId, adminId) -> {
                if (adminId != null) {
                    messagingTemplate.convertAndSend(
                            "/topic/session/" + teamId + "/admin",
                            adminId.toString()
                    );
                }
            });

            KTable<UUID, Map<UUID, UserVoteStateDTO>> voteTable = voteStream
                    .groupByKey(Grouped.with(Serdes.UUID(), voteSubmissionDTOJsonSerde))
                    .aggregate(
                            HashMap::new,
                            (teamId, voteSubmissionDTO, userVoteStateMap) -> {
                                var updatedUserVoteStateMap = new HashMap<>(userVoteStateMap);
                                if (voteSubmissionDTO.userId() == null) {
                                    updatedUserVoteStateMap.replaceAll((key, value) -> new UserVoteStateDTO(false, null));
                                } else {
                                    updatedUserVoteStateMap.put(
                                            voteSubmissionDTO.userId(),
                                            new UserVoteStateDTO(true, voteSubmissionDTO.vote())
                                    );
                                }
                                return updatedUserVoteStateMap;
                            },
                            Materialized.<UUID, Map<UUID, UserVoteStateDTO>, KeyValueStore<Bytes, byte[]>>as("vote-store")
                                    .withKeySerde(Serdes.UUID())
                                    .withValueSerde(MapSerde.userVoteStateMapSerde())
                    );

            KTable<UUID, Map<UUID, UserVoteStateDTO>> participantStateTable = userPresenceTable
                    .outerJoin(voteTable,
                            (userPresenceState, voteState) -> {
                                if (userPresenceState != null) {
                                    return userPresenceState.entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    entry -> {
                                                        if (voteState != null) {
                                                            UserVoteStateDTO userVoteState = voteState.get(entry.getKey());
                                                            if (userVoteState != null) {
                                                                return new UserVoteStateDTO(userVoteState.voted(), userVoteState.score());
                                                            }
                                                        }
                                                        return new UserVoteStateDTO(false, null);
                                                    }
                                            ));
                                }
                                return voteState;
                            },
                            Materialized.<UUID, Map<UUID, UserVoteStateDTO>, KeyValueStore<Bytes, byte[]>>as("participant-state-store")
                                    .withKeySerde(Serdes.UUID())
                                    .withValueSerde(MapSerde.userVoteStateMapSerde())
                    );

            KStream<UUID, VotingStatusChangeDTO> votingStatusStream = streamsBuilder.stream(
                    TopicConfig.VOTING_STATUS_TOPIC,
                    Consumed.with(Serdes.String(), votingStatusChangeDTOJsonSerde)
            ).selectKey((key, value) -> UUID.fromString(key));


            KTable<UUID, VotingStatusChangeDTO> votingStatusKTable = votingStatusStream.toTable(
                    Materialized.<UUID, VotingStatusChangeDTO, KeyValueStore<Bytes, byte[]>>as("voting-status-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(votingStatusChangeDTOJsonSerde)
            );

            votingStatusKTable.toStream().foreach((teamId, votingStatusChangeDTO) -> {
                if (votingStatusChangeDTO != null) {
                    messagingTemplate.convertAndSend(
                            "/topic/session/" + teamId + "/state",
                            votingStatusChangeDTO
                    );
                }
            });

            KTable<UUID, Map<UUID, UserVoteStateDTO>> maskedParticipantStateTable = participantStateTable.leftJoin(
                    votingStatusKTable,
                    (participantVotes, votingStatus) -> {
                        if (votingStatus != null && votingStatus.votingState() != null) {
                            switch (votingStatus.votingState()) {
                                case ACTIVE:
                                    return participantVotes.entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    entry -> new UserVoteStateDTO(entry.getValue().voted(), null)
                                            ));
                                case IDLE:
                                    return participantVotes.entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    entry -> new UserVoteStateDTO(false, null)
                                            ));
                                case REVEALED:
                                    return participantVotes;
                                default:
                                    throw new IllegalStateException("Unexpected voting state: " + votingStatus.votingState());
                            }
                        }
                        return participantVotes;
                    },
                    Materialized.<UUID, Map<UUID, UserVoteStateDTO>, KeyValueStore<Bytes, byte[]>>as("masked-participants-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(MapSerde.userVoteStateMapSerde())
            );

            maskedParticipantStateTable.toStream().foreach((teamId, sessionState) -> {
                if (sessionState != null) {
                    messagingTemplate.convertAndSend(
                            "/topic/session/" + teamId + "/participants",
                            sessionStateMapper.getParticipants(sessionState)
                    );
                }
            });

            return userPresenceActionStream;
        }
    }

    @Bean
    public KStream<UUID, SelectedTaskDTO> taskSelectionStream(StreamsBuilder streamsBuilder) {
        try (JsonSerde<SelectedTaskDTO> selectedTaskDTOJsonSerde = new JsonSerde<>(SelectedTaskDTO.class)) {

            KStream<UUID, SelectedTaskDTO> taskSelectionStream = streamsBuilder.stream(
                    TopicConfig.TASK_SELECTION_TOPIC,
                    Consumed.with(Serdes.String(), selectedTaskDTOJsonSerde)

            ).selectKey((key, value) -> UUID.fromString(key));

            KTable<UUID, SelectedTaskDTO> taskSelectionKTable = taskSelectionStream.toTable(
                    Materialized.<UUID, SelectedTaskDTO, KeyValueStore<Bytes, byte[]>>as("task-selection-store")
                            .withKeySerde(Serdes.UUID())
                            .withValueSerde(selectedTaskDTOJsonSerde)
            );
            taskSelectionKTable.toStream().foreach((teamId, selectedTaskDTO) -> messagingTemplate.convertAndSend(
                    "/topic/session/" + teamId + "/task",
                    selectedTaskDTO)
            );
            return taskSelectionStream;
        }

    }

}
