package com.home.task.service;

import com.home.task.config.EmbeddedPostgresConfiguration;
import com.home.task.config.EmbeddedPostgresWithFlywayConfiguration;
import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith({EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class, SpringExtension.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresWithFlywayConfiguration.class})
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Test
    @Transactional
    void testConcurrency() throws InterruptedException {
        UUID requestId = UUID.randomUUID();
        UUID requestId1 = UUID.randomUUID();
        TaskRunRequest taskRunRequest = TaskRunRequest.builder()
                .count(10)
                .min(1)
                .max(10)
                .requestId(requestId)
                .build();
        TaskRunRequest taskRunRequest1 = TaskRunRequest.builder()
                .count(10)
                .min(1)
                .max(10)
                .requestId(requestId1)
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> callableTask = () -> {
            taskService.runAsyncTask(taskRunRequest);
            return "Task ID " + taskRunRequest.getTaskId() + " with request ID " + taskRunRequest.getRequestId();
        };

        Callable<String> callableTask1 = () -> {
            taskService.runAsyncTask(taskRunRequest1);
            return "Task ID " + taskRunRequest1.getTaskId() + " with request ID " + taskRunRequest1.getRequestId();
        };

        List<Callable<String>> callableTasks = new ArrayList<>();
        callableTasks.add(callableTask);
        callableTasks.add(callableTask1);

        executor.invokeAll(callableTasks);

        Optional<TaskEntity> entityOptional = taskService.getTaskResult(taskRunRequest.getRequestId());
        Optional<TaskEntity> entityOptional1 = taskService.getTaskResult(taskRunRequest1.getRequestId());

        assertThat(entityOptional).isNotEmpty();
        assertThat(entityOptional1).isNotEmpty();
        assertThat(entityOptional.get().isSuccessful()).isTrue();
        assertThat(entityOptional.get().isSuccessful()).isFalse();
    }
}
