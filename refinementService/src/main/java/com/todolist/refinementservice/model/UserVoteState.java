package com.todolist.refinementservice.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVoteState {
    private boolean voted;
    private Integer score;
}
