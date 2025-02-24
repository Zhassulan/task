package com.home.task.service;

import com.home.task.dto.TaskRunRequest;
import com.home.task.repository.TasksJpaRepository;
import com.home.task.runnable.TaskExecutionRunnableTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service("taskService")
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final LockRegistry lockRegistry;
    private final TasksJpaRepository tasksJpaRepository;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Override
    public void run(TaskRunRequest request) {
        threadPoolTaskScheduler.execute(TaskExecutionRunnableTask.builder().lockRegistry(lockRegistry)
                .request(request)
                .tasksJpaRepository(tasksJpaRepository)
                .build());
    }
}
