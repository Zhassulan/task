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

    public TaskExecutionRunnableTask(TaskRunRequest request) {
        this.request = request;
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

    @Override
    public void run() {
        run(request);
    }
}
