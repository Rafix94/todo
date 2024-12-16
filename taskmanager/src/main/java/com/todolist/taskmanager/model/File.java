package com.todolist.taskmanager.model;

import com.todolist.ScanningStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String contentType;

    @Column
    @Enumerated(EnumType.STRING)
    private ScanningStatus scanningStatus;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;
}