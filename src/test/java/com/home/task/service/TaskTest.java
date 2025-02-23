package com.home.task.service;

import com.home.task.config.EmbeddedPostgresConfiguration;
import com.home.task.config.EmbeddedPostgresWithFlywayConfiguration;
import com.home.task.config.JdbcConfig;
import com.home.task.config.JpaConfig;
import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import com.home.task.runnable.TaskExecutionRunnableTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith({EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresWithFlywayConfiguration.class})
@Import({JdbcConfig.class, JpaConfig.class})
@ActiveProfiles("test")
public class TaskTest {

    @Autowired
    private JdbcLockRegistry lockRegistry;

    @Autowired
    private TasksJpaRepository tasksJpaRepository;

    @Test
    void testTwoLongTasksOneShouldHasFalseResult() throws InterruptedException, ExecutionException {
        UUID requestId = UUID.randomUUID();
        UUID requestId1 = UUID.randomUUID();
        TaskRunRequest taskRunRequest = TaskRunRequest.builder()
                .count(10000000)
                .min(1)
                .max(10000000)
                .requestId(requestId)
                .build();
        TaskRunRequest taskRunRequest1 = TaskRunRequest.builder()
                .count(10000000)
                .min(1)
                .max(10000000)
                .requestId(requestId1)
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Future<?>> futures = new ArrayList<>();

        Future<?> f = executor.submit(TaskExecutionRunnableTask.builder().lockRegistry(lockRegistry)
                .request(taskRunRequest)
                .tasksJpaRepository(tasksJpaRepository)
                .build());
        Future<?> f1 = executor.submit(TaskExecutionRunnableTask.builder().lockRegistry(lockRegistry)
                .request(taskRunRequest1)
                .tasksJpaRepository(tasksJpaRepository)
                .build());
        futures.addAll(List.of(f, f1));

        for (Future<?> fv : futures) {
            fv.get();
        }

        Optional<TaskEntity> entityOptional = tasksJpaRepository.findByRequestId(taskRunRequest.getRequestId());
        Optional<TaskEntity> entityOptional1 = tasksJpaRepository.findByRequestId(taskRunRequest1.getRequestId());

        assertThat(entityOptional).isNotEmpty();
        assertThat(entityOptional1).isNotEmpty();

        List<Boolean> results = List.of(entityOptional.get().isSuccessful(), entityOptional1.get().isSuccessful());
        assertThat(results).satisfiesAnyOf(booleans -> booleans.equals(true));
        assertThat(results).satisfiesAnyOf(booleans -> booleans.equals(false));
    }

    @Test
    void testTwoShortTasksBothShouldHaveTrueResult() throws InterruptedException, ExecutionException {
        UUID requestId = UUID.randomUUID();
        UUID requestId1 = UUID.randomUUID();
        TaskRunRequest taskRunRequest = TaskRunRequest.builder()
                .count(100000)
                .min(1)
                .max(100000)
                .requestId(requestId)
                .build();
        TaskRunRequest taskRunRequest1 = TaskRunRequest.builder()
                .count(100000)
                .min(1)
                .max(100000)
                .requestId(requestId1)
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<Future<?>> futures = new ArrayList<>();

        Future<?> f = executor.submit(TaskExecutionRunnableTask.builder().lockRegistry(lockRegistry)
                .request(taskRunRequest)
                .tasksJpaRepository(tasksJpaRepository)
                .build());
        Future<?> f1 = executor.submit(TaskExecutionRunnableTask.builder().lockRegistry(lockRegistry)
                .request(taskRunRequest1)
                .tasksJpaRepository(tasksJpaRepository)
                .build());
        futures.addAll(List.of(f, f1));

        for (Future<?> fv : futures) {
            fv.get();
        }

        Optional<TaskEntity> entityOptional = tasksJpaRepository.findByRequestId(taskRunRequest.getRequestId());
        Optional<TaskEntity> entityOptional1 = tasksJpaRepository.findByRequestId(taskRunRequest1.getRequestId());

        assertThat(entityOptional).isNotEmpty();
        assertThat(entityOptional1).isNotEmpty();

        List<Boolean> results = List.of(entityOptional.get().isSuccessful(), entityOptional1.get().isSuccessful());
        assertThat(results).satisfiesAnyOf(booleans -> booleans.equals(true));
        assertThat(results).satisfiesAnyOf(booleans -> booleans.equals(true));
    }
}
