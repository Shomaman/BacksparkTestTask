package com.example.Backspark.TestTask.SockBatch;

import com.example.Backspark.TestTask.entity.SockEntity;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
class SockItemWriterTest {

    @Autowired
    JdbcBatchItemWriter<SockBatchPayload> sockItemWriter;

    @Autowired
    DataSource dataSource;

    @Test
    @Sql("/sql/schema.sql")
    @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/drop.sql")
    void dbWriterWithSockBatchPayload_SuccessWrite() throws Exception {
        //given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        List<SockBatchPayload> list = new ArrayList<>();
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", 10d, 15d);
        list.add(sockBatchPayload);
        // when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            sockItemWriter.write(list);
            return null;
        });
        //then
        JdbcOperations template = new JdbcTemplate(this.dataSource);
        String sql = "select * from sock where sock_color = ? and cotton = ? and quantity = ?";
        SockEntity sockEntity = template.queryForObject(sql,
                new BeanPropertyRowMapper<>(SockEntity.class), "red", 10, 15);
        assertNotNull(sockEntity);
        assertEquals(new SockEntity("red", 10d, 15d), sockEntity);
    }

}