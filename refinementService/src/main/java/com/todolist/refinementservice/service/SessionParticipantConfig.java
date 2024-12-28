package com.todolist.refinementservice.service;

import com.todolist.refinementservice.config.TopicConfig;
import com.todolist.refinementservice.dto.UserPresenceActionDTO;
import com.todolist.refinementservice.dto.VoteSubmissionDTO;
import com.todolist.refinementservice.dto.VotingStatusChangeDTO;
import com.todolist.refinementservice.mapper.SessionStateMapper;
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

            KTable<UUID, Map<UUID, UserVoteState>> userPresenceTable = userPresenceActionStream
                    .groupByKey(Grouped.with(Serdes.UUID(), userPresenceActionDTOJsonSerde))
                    .aggregate(
                            HashMap::new,
                            (teamId, userPresenceActionDTO, userVoteStateMap) -> {
                                var updatedUserPresenceStateMap = new HashMap<>(userVoteStateMap);

                                switch (userPresenceActionDTO.presenceAction()) {
                                    case JOIN -> {
                                        updatedUserPresenceStateMap.put(
                                                userPresenceActionDTO.userId(),
                                                new UserVoteState(false, null));
                                    }
                                    case LEAVE -> {
                                        updatedUserPresenceStateMap.remove(userPresenceActionDTO.userId());
                                        if (updatedUserPresenceStateMap.isEmpty()) {
                                            return null;
                                        }
                                    }
                                }
                                return updatedUserPresenceStateMap;
                            },
                            Materialized.<UUID, Map<UUID, UserVoteState>, KeyValueStore<Bytes, byte[]>>as("user-presence-store")
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

            KTable<UUID, Map<UUID, UserVoteState>> voteTable = voteStream
                    .groupByKey(Grouped.with(Serdes.UUID(), voteSubmissionDTOJsonSerde))
                    .aggregate(
                            HashMap::new,
                            (teamId, voteSubmissionDTO, userVoteStateMap) -> {
                                var updatedUserVoteStateMap = new HashMap<>(userVoteStateMap);
                                if (voteSubmissionDTO.userId() == null) {
                                    updatedUserVoteStateMap.replaceAll((key, value) -> new UserVoteState(false, null));
                                } else {
                                    updatedUserVoteStateMap.put(
                                            voteSubmissionDTO.userId(),
                                            new UserVoteState(true, voteSubmissionDTO.vote())
                                    );
                                }
                                return updatedUserVoteStateMap;
                            },
                            Materialized.<UUID, Map<UUID, UserVoteState>, KeyValueStore<Bytes, byte[]>>as("vote-store")
                                    .withKeySerde(Serdes.UUID())
                                    .withValueSerde(MapSerde.userVoteStateMapSerde())
                    );

            KTable<UUID, Map<UUID, UserVoteState>> participantStateTable = userPresenceTable
                    .outerJoin(voteTable,
                            (userPresenceState, voteState) ->
                                    userPresenceState.entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    entry -> {
                                                        if (voteState != null) {
                                                            UserVoteState userVoteState = voteState.get(entry.getKey());
                                                            if (userVoteState != null) {
                                                                return new UserVoteState(userVoteState.voted(), userVoteState.score());
                                                            }
                                                        }
                                                        return new UserVoteState(false, null);
                                                    }
                                            )),
                            Materialized.<UUID, Map<UUID, UserVoteState>, KeyValueStore<Bytes, byte[]>>as("participant-state-store")
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

            KTable<UUID, Map<UUID, UserVoteState>> maskedParticipantStateTable = participantStateTable.leftJoin(
                    votingStatusKTable,
                    (participantVotes, votingStatus) -> {
                        if (votingStatus != null && votingStatus.votingState() != null) {
                            switch (votingStatus.votingState()) {
                                case ACTIVE:
                                    return participantVotes.entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    entry -> new UserVoteState(entry.getValue().voted(), null)
                                            ));
                                case IDLE:
                                    return participantVotes.entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    entry -> new UserVoteState(false, null)
                                            ));
                                case REVEALED:
                                    return participantVotes;
                                default:
                                    throw new IllegalStateException("Unexpected voting state: " + votingStatus.votingState());
                            }
                        }
                        return participantVotes;
                    },
                    Materialized.<UUID, Map<UUID, UserVoteState>, KeyValueStore<Bytes, byte[]>>as("masked-participants-store")
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
}
