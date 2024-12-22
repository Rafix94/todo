package com.todolist.refinementservice.dto;

import java.util.List;

public record SessionParticipantsDTO (
        List<ParticipantDTO> participants
){}
