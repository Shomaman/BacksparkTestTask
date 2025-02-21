package com.example.Backspark.TestTask.SockBatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBatchTest
@SpringBootTest
class SockJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @BeforeEach
    public void setUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @Sql("/sql/schema.sql")
    @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/drop.sql")
    void importReportJobWithValidFile_SuccessJob() throws Exception {
        // given
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.csv", "fileWithValidData.csv");

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(paramsBuilder.toJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertEquals("import-sockFromCSVFile-job", actualJobInstance.getJobName());
        assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
    }

    @Test
    @Sql("/sql/schema.sql")
    @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/drop.sql")
    void importReportJobWithInvalidFile_FailedJob() throws Exception {
        // given
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.csv", "fileWithoutDelim.csv");

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(paramsBuilder.toJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // then
        assertEquals("import-sockFromCSVFile-job", actualJobInstance.getJobName());
        assertEquals("FAILED", actualJobExitStatus.getExitCode());
    }
}