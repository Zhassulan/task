package com.home.task.batch;

import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
@Slf4j
public class TaskProcessor implements org.springframework.batch.item.ItemProcessor<TaskRunRequest, TaskEntity> {

    private static final Long ID = 1L;
    private TaskRunRequest request;
    private LockRegistry lockRegistry;
    private TasksJpaRepository tasksJpaRepository;

    @Override
    public TaskEntity process(TaskRunRequest request) throws Exception {
        return run(request);
    }

    private TaskEntity run(TaskRunRequest request) {
        log.info("Running task by request {}", request);
        var lock = lockRegistry.obtain(String.valueOf(ID));
        boolean lockAquired = lock.tryLock();

        if (lockAquired) {
            log.info("Lock taken successfully for task ID {}", ID);
            try {
                AtomicInteger counter = new AtomicInteger(0);
                Stream<Integer> stream = Stream
                        .generate(() -> {
                            counter.incrementAndGet();
                            int random = (int) (Math.random() * request.getMax() + request.getMin());
                            return random;
                        })
                        .takeWhile(n -> counter.get() < request.getCount());

                log.info("Task ID {} is completed successfully by request {}", ID, request.getRequestId());

                return tasksJpaRepository.save(TaskEntity.builder().successful(true)
                        .message("Successfully finished task ID " + ID + " by request ID " + request.getRequestId())
                        .taskId(ID)
                        .requestId(request.getRequestId())
                        .result(stream.toArray(Integer[]::new))
                        .build());

            } finally {
                lock.unlock();
                log.info("Lock untaken successfully for task ID {} by request ID {}", ID, request.getRequestId());
            }
        } else {
            log.error("Lock error for task ID {} by request ID {}", ID, request.getRequestId());
            return tasksJpaRepository.save(TaskEntity.builder()
                    .message("Lock error on running task ID " + ID + " by request ID " + request.getRequestId())
                    .taskId(ID)
                    .requestId(request.getRequestId())
                    .build());
        }
    }
}
