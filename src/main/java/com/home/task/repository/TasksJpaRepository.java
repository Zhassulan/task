package com.home.task.repository;

import com.home.task.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TasksJpaRepository extends JpaRepository<TaskEntity, Long>, PagingAndSortingRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByRequestId(UUID id);
}
