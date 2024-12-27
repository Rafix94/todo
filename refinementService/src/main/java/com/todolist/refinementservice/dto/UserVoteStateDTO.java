package com.todolist.refinementservice.dto;

public record UserVoteStateDTO(
        Boolean voted,
        Integer score
) {}
