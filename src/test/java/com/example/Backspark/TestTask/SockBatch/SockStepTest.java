package com.example.Backspark.TestTask.SockBatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@SpringBatchTest
public class SockStepTest {

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    public void setUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @Sql("/sql/schema.sql")
    @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/drop.sql")
    void loadCsvStepWithValidFile_SuccessStep() {
        //given
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.csv", "fileWithValidData.csv");

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("load-csv-file", paramsBuilder.toJobParameters());
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualExitStatus = jobExecution.getExitStatus();
        // then
        assertEquals(1, actualStepExecutions.size());
        assertEquals("COMPLETED", actualExitStatus.getExitCode());
        actualStepExecutions.forEach(stepExecution -> assertEquals(10, stepExecution.getWriteCount()));
    }

    @Test
    @Sql("/sql/schema.sql")
    @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/drop.sql")
    void loadCsvStepWithValidFile_FailedStep() {
        //given
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.csv", "fileWithoutDelim.csv");

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("load-csv-file", paramsBuilder.toJobParameters());
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualExitStatus = jobExecution.getExitStatus();
        // then
        assertEquals(1, actualStepExecutions.size());
        assertEquals("FAILED", actualExitStatus.getExitCode());
    }

}
