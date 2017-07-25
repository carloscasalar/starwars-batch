package com.starwars.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job started at " + jobExecution.getStartTime());
        jobExecution.getStepExecutions().forEach(step -> {
            log.info("step times:", step.getStartTime(), step.getEndTime());
        });
    }
}
