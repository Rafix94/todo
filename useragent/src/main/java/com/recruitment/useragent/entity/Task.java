package com.recruitment.useragent.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Task {
    @Id
    private Long id;

    private String title;
    private String description;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private String category;
}
