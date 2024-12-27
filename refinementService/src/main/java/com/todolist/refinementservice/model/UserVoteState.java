package com.todolist.refinementservice.model;

public record UserVoteState (
        boolean voted,
        Integer score
)
{}
