package com.home.task.batch;

import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEntityWriter implements ItemWriter<TaskEntity> {

    private final TasksJpaRepository repository;

    @Override
    public void write(Chunk<? extends TaskEntity> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}
