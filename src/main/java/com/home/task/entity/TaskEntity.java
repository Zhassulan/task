package com.home.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "task_result")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "result")
    private Integer[] result;

    @Column(name = "created")
    private Date created;

    @Column(name = "successful")
    private boolean successful;

    @Column(name = "message")
    private String message;
}
