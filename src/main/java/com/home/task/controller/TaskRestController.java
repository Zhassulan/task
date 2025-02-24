package com.home.task.controller;

import com.home.task.dto.RequestId;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import com.home.task.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskRestController {

    private static final int TASK_ID = 1;

    @Autowired
    @Qualifier("taskBatchService")
    private final TaskService taskService;

    private final TasksJpaRepository repository;

    @PostMapping("/task")
    public RequestId runTaskAsync(@RequestParam(value = "min") @NotNull int min,
                                  @RequestParam(value = "max") @NotNull int max,
                                  @RequestParam(value = "count") @NotNull int count) {

        UUID uuid = UUID.randomUUID();
        RequestId requestId = RequestId.builder()
                .id(uuid)
                .build();
        repository.save(TaskEntity.builder()
                .requestId(uuid)
                .min(min)
                .max(max)
                .taskId(TASK_ID)
                .count(count)
                .created(LocalDateTime.now())
                .build());
        taskService.run();

        return requestId;
    }

    @GetMapping("/task")
    public ResponseEntity getTaskResult(@RequestParam(value = "requestId") @NotNull UUID id) {
        Optional<TaskEntity> task = repository.findByRequestId(id);

        return task
                .map(t -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
}
