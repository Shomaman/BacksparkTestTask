package com.example.Backspark.TestTask.controller;

import com.example.Backspark.TestTask.controller.payload.FilterSocksPayload;
import com.example.Backspark.TestTask.controller.payload.SockPayload;
import com.example.Backspark.TestTask.entity.SockEntity;
import com.example.Backspark.TestTask.exceptionHandling.NotEnoughSocksException;
import com.example.Backspark.TestTask.exceptionHandling.SockBatchException;
import com.example.Backspark.TestTask.service.SockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SockControllerTest {
    @Mock
    SockService service;

    @InjectMocks
    SockController controller;

    @Test
    void addSockWithValidPayload_ReturnsSockEntity() throws BindException {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        doReturn(new SockEntity("blue", 70d, 50d))
                .when(this.service).addSock(payload);
        //when
        var result = this.controller.addSock(payload, bindingResult);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(new SockEntity("blue", 70d, 50d), result.getBody());
        verify(this.service).addSock(payload);
        verifyNoMoreInteractions(this.service);
    }

    @Test
    void addSockWithInvalidPayload_ThrowsBindException() {
        //given
        var payload = new SockPayload(null, 1d, 1d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "sockColor", "error"));

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.addSock(payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "sockColor", "error")), exception.getAllErrors());
        verifyNoInteractions(this.service);
    }

    @Test
    void expenseSockWithValidPayload_ReturnsSockEntity() throws NotEnoughSocksException, BindException {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        doReturn(new SockEntity("blue", 70d, 50d))
                .when(this.service).expenseSock(payload);
        //when
        var result = this.controller.expenseSock(payload, bindingResult);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(new SockEntity("blue", 70d, 50d), result.getBody());
        verify(this.service).expenseSock(payload);
        verifyNoMoreInteractions(this.service);
    }

    @Test
    void expenseSockWithInvalidPayload_ThrowsBindException() {
        //given
        var payload = new SockPayload(null, 1d, 1d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "sockColor", "error"));

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.expenseSock(payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "sockColor", "error")), exception.getAllErrors());
        verifyNoInteractions(this.service);
    }

    @Test
    void getAllSocksWithoutFilter_ReturnsListOfSockEntity() {
        //given
        var expected = List.of(new SockEntity("blue", 70d, 50d),
                new SockEntity("red", 50d, 70d));
        doReturn(expected).when(this.service).getAllSocks(null);
        //when
        var result = this.controller.getAllSocks(null);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected, result.getBody());
        verify(this.service).getAllSocks(null);
        verifyNoMoreInteractions(this.service);
    }

    @Test
    void getAllSocksWithFilter_ReturnsListOfSockEntity() {
        //given
        var expected = List.of(new SockEntity("blue", 70d, 50d));
        var filter = new FilterSocksPayload("blue", null, null,
                null, null, null, null);
        doReturn(expected).when(this.service).getAllSocks(filter);
        //when
        var result = this.controller.getAllSocks(filter);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(new SockEntity("blue", 70d, 50d)), result.getBody());
        verify(this.service).getAllSocks(filter);
        verifyNoMoreInteractions(this.service);
    }

    @Test
    void putSockWithValidPayload_ReturnsSockEntity() throws BindException {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        var id = 1;
        doReturn(new SockEntity("blue", 70d, 50d))
                .when(this.service).putSock(id, payload);
        //when
        var result = this.controller.putSock(payload, bindingResult, id);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(new SockEntity("blue", 70d, 50d), result.getBody());
        verify(this.service).putSock(id, payload);
        verifyNoMoreInteractions(this.service);
    }

    @Test
    void putSockWithInvalidPayload_ThrowsBindException() {
        //given
        var payload = new SockPayload(null, 1d, 1d);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        var id = 1;
        bindingResult.addError(new FieldError("payload", "sockColor", "error"));

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.putSock(payload, bindingResult, id));

        // then
        assertEquals(List.of(new FieldError("payload", "sockColor", "error")), exception.getAllErrors());
        verifyNoInteractions(this.service);
    }

    @Test
    void batchSockWithValidFile_ReturnsInteger() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException, SockBatchException {
        //given
        var file = new MockMultipartFile("file.xls", new byte[0]);
        doReturn(1).when(this.service).batchSock(file);
        //when
        var result = this.controller.batchSock(file);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Было добавлено " + 1 + " записей", result.getBody());
        verify(this.service).batchSock(file);
        verifyNoMoreInteractions(this.service);
    }
}