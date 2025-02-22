package com.home.task.controller;

import com.home.task.dto.RequestId;
import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import com.home.task.service.TaskService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskRestController {

    private final TaskService taskService;
    private final TasksJpaRepository tasksJpaRepository;

    @PostMapping("/task")
    public RequestId runTaskAsync(@RequestParam(value = "min") @NotNull int min,
                                  @RequestParam(value = "max") @NotNull int max,
                                  @RequestParam(value = "count") @NotNull int count) {

        RequestId requestId = RequestId.builder()
                .id(UUID.randomUUID())
                .build();
        TaskRunRequest req = TaskRunRequest.builder()
                .max(max)
                .min(min)
                .count(count)
                .requestId(requestId.getId())
                .build();

        taskService.runAsyncTask(req);

        return requestId;
    }

    @GetMapping("/task")
    public ResponseEntity getTaskResult(@RequestParam(value = "requestId") @NotNull UUID id) {
        Optional<TaskEntity> task = tasksJpaRepository.findByRequestId(id);

        return task
                .map(t -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
}
