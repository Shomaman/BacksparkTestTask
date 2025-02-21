package com.example.Backspark.TestTask.exceptionHandling;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class SockExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<String>> handleBindException(BindException exception) {
        return ResponseEntity.badRequest().body(exception.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .toList());
    }

    @ExceptionHandler(NotEnoughSocksException.class)
    public ResponseEntity<List<String>> handleNotEnoughSocksException(NotEnoughSocksException exception) {
        return new ResponseEntity<>(List.of(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<List<String>> handleNoSuchElementException(NoSuchElementException exception) {
        return new ResponseEntity<>(List.of(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectValueException.class)
    public ResponseEntity<List<String>> handleIncorrectValueException(IncorrectValueException exception) {
        return new ResponseEntity<>(List.of(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SockBatchException.class)
    public ResponseEntity<List<String>> handleSockBatchException(SockBatchException exception) {
        return new ResponseEntity<>(List.of(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    public ResponseEntity<List<String>> handleJobInstanceAlreadyCompleteException() {
        return new ResponseEntity<>(List.of("Данная работа уже выполнена"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobExecutionAlreadyRunningException.class)
    public ResponseEntity<List<String>> handleJobExecutionAlreadyRunningException(){
        return new ResponseEntity<>(List.of("Данная работа уже выполняется"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobParametersInvalidException.class)
    public ResponseEntity<List<String>> handleJobParametersInvalidException() {
        return new ResponseEntity<>(List.of("Заданы неверные параметры для работы"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobRestartException.class)
    public ResponseEntity<List<String>> handleJobRestartException() {
        return new ResponseEntity<>(List.of("Ранее во время выполнения задания произошла ошибка," +
                " на данный момент запуск задания невозможен"), HttpStatus.BAD_REQUEST);
    }
}
