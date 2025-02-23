package com.home.task.repository;

import com.home.task.config.EmbeddedPostgresConfiguration;
import com.home.task.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@ActiveProfiles("test")
public class RepositoryTest {

    private Long TASK_ID = 1L;

    @Autowired
    private TasksJpaRepository tasksJpaRepository;

    @Test
    void testSaveShouldFindSavedEntity() {
        UUID requestId = UUID.randomUUID();
        TaskEntity newTask = TaskEntity.builder().taskId(TASK_ID)
                .requestId(requestId)
                .build();
        TaskEntity insertedTask = tasksJpaRepository.save(newTask);

        assertThat(tasksJpaRepository.findById(insertedTask.getId())).isNotEmpty();
        assertThat(tasksJpaRepository.findById(insertedTask.getId()).get()).isEqualTo(newTask);
    }

    @Test
    void testFindByRequestIdShouldReturnObject() {
        UUID requestId = UUID.randomUUID();
        TaskEntity newTask = TaskEntity.builder().taskId(TASK_ID)
                .requestId(requestId)
                .build();
        tasksJpaRepository.save(newTask);

        assertThat(tasksJpaRepository.findByRequestId(newTask.getRequestId())).isNotEmpty();
    }
}
