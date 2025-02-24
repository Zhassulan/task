package com.home.task.repository;

import com.home.task.entity.TaskEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;


public interface TasksJpaRepository extends CrudRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByRequestId(UUID id);

}
