package com.example.Backspark.TestTask.service;

import com.example.Backspark.TestTask.controller.payload.FilterSocksPayload;
import com.example.Backspark.TestTask.controller.payload.SockPayload;
import com.example.Backspark.TestTask.entity.SockEntity;
import com.example.Backspark.TestTask.exceptionHandling.NotEnoughSocksException;
import com.example.Backspark.TestTask.exceptionHandling.SockBatchException;
import com.example.Backspark.TestTask.repository.SockCriteriaBuilder;
import com.example.Backspark.TestTask.repository.SockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;

@Service
public class SockService {
    private final Logger log = LoggerFactory.getLogger(SockService.class);
    private final SockRepository sockRepository;
    private final Job job;
    private final JobLauncher jobLauncher;
    private final SockCriteriaBuilder builder;

    public SockService(SockRepository sockRepository, Job job,
                       JobLauncher jobLauncher, SockCriteriaBuilder builder) {
        this.sockRepository = sockRepository;
        this.job = job;
        this.jobLauncher = jobLauncher;
        this.builder = builder;
    }

    public SockEntity addSock(SockPayload payload) {
        Optional<SockEntity> optionalSock = sockRepository.findSocksEntityByCottonAndSockColor(payload.cotton(),
                payload.sockColor());
        log.info("В метод addSock сервиса SockService пришел пейлоад:{} в базе данных есть сущность:{}," +
                        " обработка велась в потоке:{}", payload, optionalSock.isPresent(),
                Thread.currentThread().getName());
        if (optionalSock.isPresent()) {
            SockEntity sockEntity = optionalSock.get();
            sockEntity.setQuantity(sockEntity.getQuantity() + payload.quantity());
            SockEntity updatedSockEntity = sockRepository.save(sockEntity);
            log.info("В методе addSock сервиса SockService произошло обновление сущности:{} " +
                            "в результате получилась сущность:{} обработка велась в потоке:{}",
                    sockEntity, updatedSockEntity, Thread.currentThread().getName());
            return updatedSockEntity;
        }
        SockEntity newSockEntity = sockRepository.save(new SockEntity(payload.sockColor(), payload.cotton(), payload.quantity()));
        log.info("В методе addSock сервиса SockService произошло создание сущности:{} обработка велась в потоке:{}",
                newSockEntity, Thread.currentThread().getName());
        return newSockEntity;

    }

    public SockEntity expenseSock(SockPayload payload) throws NotEnoughSocksException, NoSuchElementException {
        Optional<SockEntity> optionalSock = sockRepository.findSocksEntityByCottonAndSockColor(payload.cotton(),
                payload.sockColor());
        log.info("В метод expenseSock сервиса SockService пришел пейлоад:{} в базе данных есть сущность:{}," +
                        " обработка велась в потоке:{}", payload, optionalSock.isPresent(),
                Thread.currentThread().getName());
        if (optionalSock.isPresent()) {
            SockEntity sockEntity = optionalSock.get();
            if (sockEntity.getQuantity() > payload.quantity()) {
                sockEntity.setQuantity(sockEntity.getQuantity() - payload.quantity());
                SockEntity updatedSockEntity = sockRepository.save(sockEntity);
                log.info("В методе expenseSock сервиса SockService произошло обновление сущности:{} " +
                                "в результате получилась сущность:{} обработка велась в потоке:{}",
                        sockEntity, updatedSockEntity, Thread.currentThread().getName());
                return updatedSockEntity;
            } else {
                log.info("В методе expenseSock сервиса SockService выброшено исключение NotEnoughSocksException, " +
                                "sockEntity: {} payload:{} обработка велась в потоке:{}",
                        sockEntity, payload, Thread.currentThread().getName());
                throw new NotEnoughSocksException("Нехватка носков на складе");
            }
        } else {
            log.info("В методе expenseSock сервиса SockService выброшено исключение NoSuchElementException, " +
                    "payload:{} обработка велась в потоке:{}", payload, Thread.currentThread().getName());
            throw new NoSuchElementException("Носков с таким цветом и содержанием хлопка на складе не найдено");
        }
    }

    public List<SockEntity> getAllSocks(FilterSocksPayload payload) {
        log.info("В метод getAllSocks сервиса SockService пришел пейлоад:{} обработка велась в потоке:{}",
                payload, Thread.currentThread().getName());
        if (payload == null) {
            List<SockEntity> sockEntityList = sockRepository.findAll();
            log.info("Метод getAllSocks сервиса SockService возвращает список всех сущностей, " +
                    "обработка велась в потоке:{}", Thread.currentThread().getName());
            return sockEntityList;
        }
        List<SockEntity> sockEntityList = sockRepository.findAll(builder.build(payload));
        log.info("Метод getAllSocks сервиса SockService возвращает список отфилированных " +
                "и/или отсортированных сущностей, обработка велась в потоке:{}", Thread.currentThread().getName());
        return sockEntityList;
    }

    public SockEntity putSock(Integer id, SockPayload payload) {
        Optional<SockEntity> optionalSock = sockRepository.findById(id);
        log.info("В метод putSock сервиса SockService пришел пейлоад:{} в базе данных есть сущность:{}," +
                        " обработка велась в потоке:{}", payload, optionalSock.isPresent(),
                Thread.currentThread().getName());
        if (optionalSock.isPresent()) {
            SockEntity sockEntity = optionalSock.get();
            SockEntity replacedSock = sockRepository.save(replaceSock(sockEntity, payload));
            log.info("В методе putSock сервиса SockService произошла замена сущности:{} " +
                            "в результате получилась сущность:{} обработка велась в потоке:{}",
                    sockEntity, replacedSock, Thread.currentThread().getName());
            return replacedSock;
        } else {
            log.info("В методе putSock сервиса SockService выброшено исключение NoSuchElementException, " +
                    "payload:{} обработка велась в потоке:{}", payload, Thread.currentThread().getName());
            throw new NoSuchElementException("Носков с таким цветом и содержанием хлопка на складе не найдено");
        }
    }

    public Integer batchSock(MultipartFile csvFile) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, SockBatchException {
        log.info("В метод batchSock сервиса SockService поступил файл:{}, размер файла:{}," +
                        "  обработка велась в потоке:{}", csvFile.toString(), csvFile.getSize(),
                Thread.currentThread().getName());
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("file.csv", Path.of(csvFile.getOriginalFilename()).toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution run = jobLauncher.run(job, jobParameters);
        Collection<StepExecution> stepExecutions = run.getStepExecutions();
        int writeCount = 0;
        List<Throwable> exceptions = new ArrayList<>();
        for (StepExecution stepExecution : stepExecutions) {
            writeCount += stepExecution.getWriteCount();
            List<Throwable> failureExceptions = stepExecution.getFailureExceptions();
            exceptions.addAll(failureExceptions);
        }
        exceptionHandler(exceptions);
        log.info("Из метода batchSock сервиса SockService " +
                "отправляем количество записанных в базу данных строк: {}, " +
                "обработка велась в потоке:{}", writeCount, Thread.currentThread().getName());
        return writeCount;
    }

    private void exceptionHandler(List<Throwable> exceptions) throws SockBatchException {
        if (!exceptions.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (Throwable throwable : exceptions) {
                if (throwable instanceof FlatFileParseException exception) {
                    message.append("Ошибка обработки файла в строке номер ").append(exception.getLineNumber()).append(" , строка ").append(exception.getInput()).append(".");
                } else {
                    message.append(throwable.getMessage()).append(".");
                }
            }
            throw new SockBatchException(message.toString());
        }
    }

    private SockEntity replaceSock(SockEntity sockEntity, SockPayload payload) {
        Double quantity = payload.quantity();
        if (quantity != null) {
            sockEntity.setQuantity(quantity);
        }
        Double cotton = payload.cotton();
        if (cotton != null) {
            sockEntity.setCotton(cotton);
        }
        String sockColor = payload.sockColor();
        if (sockColor != null) {
            sockEntity.setSockColor(sockColor);
        }
        return sockEntity;
    }
}

