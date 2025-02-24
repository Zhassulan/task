package com.home.task.batch;

import com.home.task.entity.TaskEntity;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityItemProcessor implements ItemProcessor<TaskEntity, TaskEntity> {
    @Override
    public TaskEntity process(TaskEntity item) throws Exception {
        return item;
    }
}
