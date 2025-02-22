package com.home.task.repository;

import com.home.task.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TasksJpaRepository extends JpaRepository<TaskEntity, Long> {
}
