package com.home.task.runnable;

import com.home.task.dto.TaskRunRequest;
import com.home.task.exception.TaskRunException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Getter
public class TaskExecutionRunnableTask implements Runnable {

    private static final int ID = 1;
    private TaskRunRequest request;
    private LockRegistry lockRegistry;

    public TaskExecutionRunnableTask(LockRegistry lockRegistry, TaskRunRequest request) {
        this.request = request;
        this.lockRegistry = lockRegistry;
    }

    private Stream<Integer> run(TaskRunRequest request) throws TaskRunException {
        var lock = lockRegistry.obtain(String.valueOf(request.getTaskId()));
        boolean lockAquired = lock.tryLock();

        if (lockAquired) {
            log.info("Lock taken successfully for task ID {}", request.getTaskId());
            try {
                return run(request);
            } finally {
                lock.unlock();
                log.info("Lock untaken successfully for payment ID {}", request.getTaskId());
            }
        } else {

            throw new TaskRunException("Error on running task ID " + request.getTaskId());
        }
    }

    @Override
    public void run() {
        run(request);
    }
}
