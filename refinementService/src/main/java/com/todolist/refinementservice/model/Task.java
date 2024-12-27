package com.todolist.refinementservice.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String title;
    private String description;
}
