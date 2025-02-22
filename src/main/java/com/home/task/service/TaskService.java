package com.home.task.service;

import com.home.task.dto.TaskRunRequest;
import com.home.task.exception.TaskRunException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final LockRegistry lockRegistry;

    public Stream<Integer> runTask(TaskRunRequest request) throws TaskRunException {
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

    private Stream<Integer> run(TaskRunRequest request) {
        AtomicInteger counter = new AtomicInteger(0);
        return Stream
                .generate(() -> {
                    counter.incrementAndGet();
                    int random = (int) (Math.random() * request.getMax() + request.getMin());
                    return random;
                })
                .takeWhile(n -> counter.get() < request.getCount());
    }
}
