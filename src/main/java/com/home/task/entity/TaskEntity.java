package com.home.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
    @Column(columnDefinition = "serial", nullable = false, updatable = false)
    private Long id;
    private UUID requestId;
    private Integer taskId;
    private Integer min;
    private Integer max;
    private Integer count;
    private Integer[] result;
    private boolean successful;
    private String message;
    private Timestamp created;
}
