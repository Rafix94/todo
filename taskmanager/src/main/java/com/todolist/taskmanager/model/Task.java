package com.todolist.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private String title;

    @Column(nullable = false)
    @Setter
    private String description;

    @Column(nullable = false)
    private UUID createdBy;

    @Column
    @Setter
    private UUID assignedTo;

    @PrePersist
    protected void onCreate() {
        createdBy = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
