package com.starwars.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PeopleStepListener {
    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        // this is a map shared by all process in the job
        //stepExecution.getJobExecution().getExecutionContext()

        log.info("Hello from afterStep listener. Summary:");
        log.info(stepExecution.getSummary());
    }

}
