package com.home.task.batch;

import com.home.task.dto.TaskRunRequest;
import com.home.task.entity.TaskEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
@Data
public class TaskProcessor implements ItemProcessor<TaskEntity, TaskEntity> {

    private static final Integer ID = 1;

    private final LockRegistry lockRegistry;

    private Map<String, Object> params;

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

                return TaskEntity.builder().successful(true)
                        .message("Successfully finished task ID " + ID + " by request ID " + request.getRequestId())
                        .taskId(ID)
                        .requestId(request.getRequestId())
                        .result(stream.toArray(Integer[]::new))
                        .build();

            } finally {
                lock.unlock();
                log.info("Lock untaken successfully for task ID {} by request ID {}", ID, request.getRequestId());
            }
        } else {
            log.error("Lock error for task ID {} by request ID {}", ID, request.getRequestId());

            return TaskEntity.builder()
                    .message("Lock error on running task ID " + ID + " by request ID " + request.getRequestId())
                    .taskId(ID)
                    .requestId(request.getRequestId())
                    .build();
        }
    }

    @Override
    public TaskEntity process(TaskEntity item) throws Exception {
        String requestId = params.get("requestId").toString();
        int min = Integer.parseInt(params.get("min").toString());
        int max = Integer.parseInt(params.get("max").toString());
        int count = Integer.parseInt(params.get("count").toString());

        return run(TaskRunRequest.builder()
                .requestId(UUID.fromString(requestId))
                .taskId(ID)
                .min(min)
                .max(max)
                .count(count)
                .build());
    }
}
