package com.home.task.repository;

import com.home.task.config.EmbeddedPostgresConfiguration;
import com.home.task.config.EmbeddedPostgresWithFlywayConfiguration;
import com.home.task.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresWithFlywayConfiguration.class})
@ActiveProfiles("test")
public class RepositoryTest {

    private Long TASK_ID = 1L;

    @Autowired
    private TasksJpaRepository tasksJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveShouldFindSavedEntity() {
        UUID requestId = UUID.randomUUID();
        Integer[] arr = new Integer[]{1, 2, 3};
        TaskEntity newTask = TaskEntity.builder()
                .successful(true)
                .message("test task")
                .taskId(TASK_ID)
                .requestId(requestId)
                .result(arr)
                .build();
        TaskEntity insertedTask = tasksJpaRepository.save(newTask);

        assertThat(entityManager.find(TaskEntity.class, insertedTask.getId())).isEqualTo(newTask);
    }

    @Test
    void testFindByRequestIdShouldReturnObject() {
        UUID requestId = UUID.randomUUID();
        Integer[] arr = new Integer[]{1, 2, 3};
        TaskEntity newTask = TaskEntity.builder()
                .successful(true)
                .message("test task")
                .taskId(TASK_ID)
                .requestId(requestId)
                .result(arr)
                .build();
        tasksJpaRepository.save(newTask);

        assertThat(tasksJpaRepository.findByRequestId(newTask.getRequestId())).isNotEmpty();
    }
}
