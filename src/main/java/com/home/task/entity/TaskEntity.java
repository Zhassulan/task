package com.home.task.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "tasks_result")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEntity {
    @Id
    private Long id;
    private Long taskId;
    private Integer[] result;
    private Date created;
    private boolean successful;
    private String message;
}
