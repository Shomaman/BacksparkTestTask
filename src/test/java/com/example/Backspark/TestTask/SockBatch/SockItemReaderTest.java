package com.example.Backspark.TestTask.SockBatch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SockItemReaderTest {

    @Autowired
    private FlatFileItemReader<SockBatchPayload> sockItemReader;

    @Test
    public void csvReaderWithValidFile_returnsSockBatchPayload() throws Exception {
        // given
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.csv", "fileWithValidData.csv");
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(paramsBuilder.toJobParameters());
        // when
        List<SockBatchPayload> resultList = StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            List<SockBatchPayload> list = new ArrayList<>();
            new SockBatchPayload();
            SockBatchPayload sockBatchPayload;
            sockItemReader.setEncoding("UTF-8");
            sockItemReader.open(stepExecution.getExecutionContext());
            while ((sockBatchPayload = sockItemReader.read()) != null) {
                list.add(sockBatchPayload);
            }
            return list;
        });
        //then
        assertNotNull(resultList);
        assertEquals(12, resultList.size());
    }

    @Test
    public void csvReaderWithFileWithoutDelimiter_throwsFlatFileParseException() {
        // given
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.csv", "fileWithoutDelim.csv");
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(paramsBuilder.toJobParameters());
        //when
        var exception = assertThrows(FlatFileParseException.class, () -> StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            List<SockBatchPayload> list = new ArrayList<>();
            SockBatchPayload sockBatchPayload;
            sockItemReader.setEncoding("UTF-8");
            sockItemReader.open(stepExecution.getExecutionContext());
            while ((sockBatchPayload = sockItemReader.read()) != null) {
                list.add(sockBatchPayload);
            }
            return list;
        }));
        // then
        assertEquals(2, exception.getLineNumber());
        assertEquals("blue,15,10", exception.getInput());
    }

}
