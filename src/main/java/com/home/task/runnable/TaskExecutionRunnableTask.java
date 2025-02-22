package com.home.task.runnable;

import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Getter
public class TaskExecutionRunnableTask implements Runnable {

    private static final Long ID = 1L;
    private TaskRunRequest request;
    private LockRegistry lockRegistry;
    private TasksJpaRepository tasksJpaRepository;

    public TaskExecutionRunnableTask(LockRegistry lockRegistry, TaskRunRequest request, TasksJpaRepository tasksJpaRepository) {
        this.request = request;
        this.lockRegistry = lockRegistry;
        this.tasksJpaRepository = tasksJpaRepository;
    }

    private void run(TaskRunRequest request) {
        log.info("Running task by request {}", request);
        var lock = lockRegistry.obtain(String.valueOf(ID));
        boolean lockAquired = lock.tryLock();

        if (lockAquired) {
            log.info("Lock taken successfully for task ID {}", ID);
            try {
                AtomicInteger counter = new AtomicInteger(0);
                Stream<Integer> result = Stream
                        .generate(() -> {
                            counter.incrementAndGet();
                            int random = (int) (Math.random() * request.getMax() + request.getMin());
                            return random;
                        })
                        .takeWhile(n -> counter.get() < request.getCount());
                tasksJpaRepository.save(TaskEntity.builder()
                        .successful(true)
                        .message("Successfully finished task ID " + ID)
                        .taskId(ID)
                        .requestId(request.getRequestId())
                        .result(result.toArray(Integer[]::new))
                        .build());
            } finally {
                lock.unlock();
                log.info("Lock untaken successfully for task ID {}", ID);
            }
        } else {
            tasksJpaRepository.save(TaskEntity.builder()
                    .successful(false)
                    .message("Lock error on running task ID " + ID)
                    .taskId(ID)
                    .requestId(request.getRequestId())
                    .build());
        }
    }

    @Override
    public void run() {
        run(request);
    }
}
