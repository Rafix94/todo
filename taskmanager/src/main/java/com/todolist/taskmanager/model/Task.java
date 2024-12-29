package com.todolist.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
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

    @Column
    private UUID teamId;

    @Column
    @Setter
    private Integer weight;

    @OneToMany(mappedBy = "task")
    List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        createdBy = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
