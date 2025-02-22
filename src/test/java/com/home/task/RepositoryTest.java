package com.home.task;

import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
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
@ActiveProfiles("test")
@ExtendWith(EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
public class RepositoryTest {

    @Autowired
    private TasksJpaRepository tasksJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSave() {
        Long ID = 1L;
        UUID requestId = UUID.randomUUID();
        Integer[] arr = new Integer[]{1, 2, 3};
        TaskEntity newTask = TaskEntity.builder()
                .successful(true)
                .message("Successfully finished task ID " + ID + " by request ID " + requestId)
                .taskId(ID)
                .requestId(requestId)
                .result(arr)
                .build();
        TaskEntity insertedTask = tasksJpaRepository.save(newTask);

        assertThat(entityManager.find(TaskEntity.class, insertedTask.getId())).isEqualTo(newTask);
    }
}
