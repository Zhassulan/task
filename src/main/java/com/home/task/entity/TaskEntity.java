package com.home.task.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "tasks_executions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEntity {
    private Long id;
    private Date created;
}
