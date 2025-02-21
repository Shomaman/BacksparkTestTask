package com.example.Backspark.TestTask.controller;

import com.example.Backspark.TestTask.controller.payload.FilterSocksPayload;
import com.example.Backspark.TestTask.controller.payload.SockPayload;
import com.example.Backspark.TestTask.entity.SockEntity;
import com.example.Backspark.TestTask.exceptionHandling.NotEnoughSocksException;
import com.example.Backspark.TestTask.exceptionHandling.SockBatchException;
import com.example.Backspark.TestTask.service.SockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
public class SockController {
    private final SockService sockService;
    private final Logger log = LoggerFactory.getLogger(SockController.class);

    public SockController(SockService sockService) {
        this.sockService = sockService;
    }

    @PostMapping(value = "/income", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSock(@Valid @RequestBody SockPayload payload,
                                     BindingResult bindingResult) throws BindException {
        log.info("В метод addSock контроллера SockController поступили такие данные:{}," +
                " обработка велась в потоке:{}", payload.toString(), Thread.currentThread().getName());
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        SockEntity sockEntity = sockService.addSock(payload);
        log.info("Из метода addSock контроллера SockController отправляем такие данные:{}," +
                " обработка велась в потоке:{}", sockEntity.toString(), Thread.currentThread().getName());
        return ResponseEntity.ok().body(sockEntity);
    }

    @PostMapping(value = "/outcome", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> expenseSock(@Valid @RequestBody SockPayload payload,
                                         BindingResult bindingResult) throws BindException, NotEnoughSocksException {
        log.info("В метод expenseSock контроллера SockController поступили такие данные:{}," +
                " обработка велась в потоке:{}", payload.toString(), Thread.currentThread().getName());
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        SockEntity sockEntity = sockService.expenseSock(payload);
        log.info("Из метода expenseSock контроллера SockController отправляем такие данные:{}," +
                " обработка велась в потоке:{} ", sockEntity.toString(), Thread.currentThread().getName());
        return ResponseEntity.ok(sockEntity);
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SockEntity>> getAllSocks(@RequestBody(required = false) FilterSocksPayload payload) {
        log.info("В метод getAllSocks контроллера SockController поступили такие данные: {}," +
                " обработка велась в потоке:{}", payload.toString(), Thread.currentThread().getName());
        ResponseEntity<List<SockEntity>> result = ResponseEntity.ok(sockService.getAllSocks(payload));
        log.info("Из метода getAllSocks контроллера SockController отправляем такие данные: {}," +
                " обработка велась в потоке:{}", result, Thread.currentThread().getName());
        return result;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SockEntity> putSock(@Valid @RequestBody SockPayload payload,
                                              BindingResult bindingResult,
                                              @PathVariable Integer id) throws BindException {
        log.info("В метод putSock контроллера SockController  поступили такие данные: {}," +
                " обработка велась в потоке:{}", payload.toString(), Thread.currentThread().getName());
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        SockEntity sockEntity = sockService.putSock(id, payload);
        log.info("Из метода putSock контроллера SockController отправляем такие данные: {}," +
                " обработка велась в потоке:{}", sockEntity.toString(), Thread.currentThread().getName());
        return ResponseEntity.ok(sockEntity);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> batchSock(@RequestParam("file") MultipartFile csvFile) throws
            JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException, FlatFileParseException, SockBatchException {
        log.info("В метод batchSock контроллера SockController  поступил файл:{}, размер файла:{}," +
                        "  обработка велась в потоке:{}", csvFile.toString(), csvFile.getSize(),
                Thread.currentThread().getName());
        Integer writeCount = sockService.batchSock(csvFile);
        log.info("Из метода batchSock контроллера SockController " +
                "отправляем количество записанных в базу данных строк: {}, " +
                "обработка велась в потоке:{}", writeCount, Thread.currentThread().getName());
        return ResponseEntity.ok("Было добавлено " + writeCount + " записей");
    }
}
