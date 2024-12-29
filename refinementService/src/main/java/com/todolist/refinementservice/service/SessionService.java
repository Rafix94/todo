package com.todolist.refinementservice.service;

import com.todolist.refinementservice.config.TopicConfig;
import com.todolist.refinementservice.dto.*;
import com.todolist.refinementservice.dto.VotingState;
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

    public void submitVote(VoteSubmissionDTO voteSubmissionDTO) {
        kafkaTemplate.send(TopicConfig.VOTES_TOPIC, String.valueOf(voteSubmissionDTO.teamId()), voteSubmissionDTO);
    }

    public void startVoting(TeamIdDTO teamIdDTO) {
        kafkaTemplate.send(
                TopicConfig.VOTING_STATUS_TOPIC,
                String.valueOf(teamIdDTO.teamId()),
                new VotingStatusChangeDTO(teamIdDTO.teamId(), VotingState.ACTIVE));
    }

    public void stopVoting(TeamIdDTO teamIdDTO) {
        kafkaTemplate.send(
                TopicConfig.VOTING_STATUS_TOPIC,
                String.valueOf(teamIdDTO.teamId()),
                new VotingStatusChangeDTO(teamIdDTO.teamId(), VotingState.REVEALED));
    }

    public void resetVoting(TeamIdDTO teamIdDTO) {
        kafkaTemplate.send(
                TopicConfig.VOTES_TOPIC,
                String.valueOf(teamIdDTO.teamId()),
                new VoteSubmissionDTO(teamIdDTO.teamId(), null, null));

        kafkaTemplate.send(
                TopicConfig.VOTING_STATUS_TOPIC,
                String.valueOf(teamIdDTO.teamId()),
                new VotingStatusChangeDTO(teamIdDTO.teamId(), VotingState.IDLE));
    }
}
