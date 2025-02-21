package com.example.Backspark.TestTask.SockBatch;

import com.example.Backspark.TestTask.exceptionHandling.IncorrectValueException;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SockItemProcessorTest {

    @Autowired
    ItemProcessor<SockBatchPayload, SockBatchPayload> itemProcessor;

    @Test
    void processWithValidSockBatchPayload_ReturnsSockBatchPayload() throws Exception {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", 10d, 15d);
        //when
        SockBatchPayload result = itemProcessor.process(sockBatchPayload);
        //then
        assertNotNull(result);
        assertEquals(sockBatchPayload, result);
    }

    @Test
    void processWithSockColorLengthGreaterThanMax_ThrowsIncorrectValueException() {
        //given
        String sockColor = "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901";
        SockBatchPayload sockBatchPayload = new SockBatchPayload(sockColor, 10d, 15d);
        //when

        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("SockColor value must be between 3 and 200", exception.getMessage());
    }

    @Test
    void processWithSockColorLengthLessThanMin_ThrowsIncorrectValueException() {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("", 10d, 15d);
        //when

        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("SockColor value must be between 3 and 200", exception.getMessage());
    }

    @Test
    void processWithCottonValueGreaterThanMax_ThrowsIncorrectValueException() {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", 1000d, 15d);
        //when

        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("Cotton value must be between 0 and 100", exception.getMessage());
    }

    @Test
    void processWithCottonValueLessThanMin_ThrowsIncorrectValueException() {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", -1000d, 15d);
        //when
        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("Cotton value must be between 0 and 100", exception.getMessage());
    }

    @Test
    void processWithCottonNull_ThrowsIncorrectValueException() {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", null, 15d);
        //when
        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("Cotton value must be filled", exception.getMessage());
    }

    @Test
    void processWithQuantityValueLessThanMin_ThrowsIncorrectValueException() {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", 10d, -15d);
        //when
        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("Quantity value must be greater than 0", exception.getMessage());
    }

    @Test
    void processWithQuantityNull_ThrowsIncorrectValueException() {
        //given
        SockBatchPayload sockBatchPayload = new SockBatchPayload("red", 10d, null);
        //when
        var exception = assertThrows(IncorrectValueException.class, () -> itemProcessor.process(sockBatchPayload));
        //then
        assertEquals("Quantity value must be filled", exception.getMessage());
    }
}