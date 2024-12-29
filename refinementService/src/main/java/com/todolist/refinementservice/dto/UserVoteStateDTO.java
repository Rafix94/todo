package com.todolist.refinementservice.dto;

public record UserVoteStateDTO(
        boolean voted,
        Integer score
)
{}
