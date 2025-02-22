package com.home.task.service;

import com.home.task.config.EmbeddedPostgresConfiguration;
import com.home.task.config.EmbeddedPostgresWithFlywayConfiguration;
import com.home.task.dto.TaskRunRequest;
import com.home.task.repository.TasksJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresWithFlywayConfiguration.class})
@ActiveProfiles("test")
public class TaskServiceTest {

    private Long TASK_ID = 1L;

    @Autowired
    private TasksJpaRepository tasksJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskService taskService;

    @Test
    void testConcurrency() throws InterruptedException {
        UUID requestId = UUID.randomUUID();
        UUID requestId1 = UUID.randomUUID();
        TaskRunRequest taskRunRequest = TaskRunRequest.builder()
                .count(10)
                .min(1)
                .max(10)
                .taskId(TASK_ID)
                .requestId(requestId)
                .build();
        TaskRunRequest taskRunRequest1 = TaskRunRequest.builder()
                .count(10)
                .min(1)
                .max(10)
                .taskId(TASK_ID)
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

        assertThat(tasksJpaRepository.findByRequestId(taskRunRequest.getRequestId())).isNotEmpty();
        assertThat(tasksJpaRepository.findByRequestId(taskRunRequest1.getRequestId())).isEmpty();
    }
}
