package com.home.task.config;

import com.home.task.batch.TaskProcessor;
import com.home.task.batch.TaskWriter;
import com.home.task.entity.TaskEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TaskJobConfig {

    private final TaskProcessor processor;
    private final EntityManagerFactory entityManagerFactory;
    private final TaskWriter taskWriter;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobExecution()
                .getJobParameters();
        log.info("Before step params: {}", parameters);
    }

    @Bean
    public Job processTaskJob(JobRepository jobRepository, Step step1) {
        String name = "Task Job";
        return new JobBuilder(name, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    @StepScope
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager txManager) throws Exception {

        return new StepBuilder("step1", jobRepository)
                .<TaskEntity, TaskEntity>chunk(5, txManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public JpaPagingItemReader reader() {
        return new JpaPagingItemReaderBuilder<TaskEntity>()
                .name("taskReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select r from TaskEntity r where r.id r.successful = false")
                .pageSize(1)
                .build();
    }

    @Bean
    public ItemProcessor<TaskEntity, TaskEntity> processor() {
        return processor;
    }

    @Bean
    public ItemWriter<TaskEntity> writer() throws Exception {
        return taskWriter;
    }
}
