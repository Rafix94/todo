package com.todolist.refinementservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class Participant {
    @Id
    @Column(nullable = false, name = "user_id")
    UUID userId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    Session session;

    @Column
    Integer score;
}
