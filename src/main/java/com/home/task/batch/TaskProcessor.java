package com.home.task.batch;

import com.home.task.entity.TaskEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
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

    private TaskEntity run(TaskEntity taskEntity) {
        var lock = lockRegistry.obtain(String.valueOf(ID));
        boolean lockAquired = lock.tryLock();

        if (lockAquired) {
            log.info("Lock taken successfully for task ID {}", ID);
            try {
                AtomicInteger counter = new AtomicInteger(0);
                Stream<Integer> stream = Stream
                        .generate(() -> {
                            counter.incrementAndGet();
                            int random = (int) (Math.random() * taskEntity.getMax() + taskEntity.getMin());
                            return random;
                        })
                        .takeWhile(n -> counter.get() < taskEntity.getCount());

                log.info("Task ID {} is completed successfully by request {}", ID, taskEntity.getRequestId());

                return TaskEntity.builder().successful(true)
                        .message("Successfully finished task ID " + ID + " by request ID " + taskEntity.getRequestId())
                        .taskId(ID)
                        .requestId(taskEntity.getRequestId())
                        .result(stream.toArray(Integer[]::new))
                        .build();

            } finally {
                lock.unlock();
                log.info("Lock untaken successfully for task ID {} by request ID {}", ID, taskEntity.getRequestId());
            }
        } else {
            log.error("Lock error for task ID {} by request ID {}", ID, taskEntity.getRequestId());

            return TaskEntity.builder()
                    .message("Lock error on running task ID " + ID + " by request ID " + taskEntity.getRequestId())
                    .taskId(ID)
                    .requestId(taskEntity.getRequestId())
                    .build();
        }
    }

    @Override
    public TaskEntity process(TaskEntity item) throws Exception {
        return run(item);
    }
}
