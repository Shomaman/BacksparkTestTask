package com.example.Backspark.TestTask.service;

import com.example.Backspark.TestTask.controller.payload.FilterSocksPayload;
import com.example.Backspark.TestTask.controller.payload.SockPayload;
import com.example.Backspark.TestTask.entity.SockEntity;
import com.example.Backspark.TestTask.exceptionHandling.NotEnoughSocksException;
import com.example.Backspark.TestTask.repository.SockCriteriaBuilder;
import com.example.Backspark.TestTask.repository.SockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @Mock
    SockRepository sockRepository;

    @Mock
    SockCriteriaBuilder builder;

    @Mock
    Specification<SockEntity> specification;

    @InjectMocks
    SockService sockService;

    @Test
    void addSockWhenSockIsPresent_ReturnUpdatedSockEntity() {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        Optional<SockEntity> optionalSock = Optional.of(new SockEntity("blue", 70d, 50d));
        doReturn(optionalSock)
                .when(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());
        var presentedSock = optionalSock.get();
        doReturn(presentedSock)
                .when(this.sockRepository).save(presentedSock);
        //when
        var result = this.sockService.addSock(payload);
        //then
        assertNotNull(result);
        assertEquals(new SockEntity("blue", 70d, 100d), result);
        verify(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());
        verify(this.sockRepository).save(presentedSock);
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void addSockWhenSockIsNotPresent_ReturnNewSockEntity() {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        Optional<SockEntity> optionalSock = Optional.empty();
        doReturn(optionalSock)
                .when(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());
        var createdSock = new SockEntity("blue", 70d, 50d);
        doReturn(createdSock)
                .when(this.sockRepository).save(createdSock);
        //when
        var result = this.sockService.addSock(payload);
        //then
        assertNotNull(result);
        assertEquals(new SockEntity("blue", 70d, 50d), result);
        verify(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void expenseSockWhenSockIsPresentAndQuantityEnough_ReturnUpdatedSockEntity() throws NotEnoughSocksException {
        //given
        var payload = new SockPayload("blue", 70d, 25d);
        Optional<SockEntity> optionalSock = Optional.of(new SockEntity("blue", 70d, 50d));
        doReturn(optionalSock)
                .when(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());
        var presentedSock = optionalSock.get();
        doReturn(presentedSock)
                .when(this.sockRepository).save(presentedSock);
        //when
        var result = this.sockService.expenseSock(payload);
        //then
        assertNotNull(result);
        assertEquals(new SockEntity("blue", 70d, 25d), result);
        verify(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());
        verify(this.sockRepository).save(presentedSock);
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void expenseSockWhenSockIsPresentAndQuantityNotEnough_ThrowNotEnoughSocksException() {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        Optional<SockEntity> optionalSock = Optional.of(new SockEntity("blue", 70d, 25d));
        doReturn(optionalSock)
                .when(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());

        //when
        var exception = assertThrows(NotEnoughSocksException.class,
                () -> this.sockService.expenseSock(payload));
        //then
        assertEquals("Нехватка носков на складе",exception.getMessage());
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void expenseSockWhenSockIsNotPresent_ThrowNoSuchElementException()
            throws NoSuchElementException {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        Optional<SockEntity> optionalSock = Optional.empty();
        doReturn(optionalSock)
                .when(this.sockRepository).findSocksEntityByCottonAndSockColor(payload.cotton(), payload.sockColor());

        //when
        var exception = assertThrows(NoSuchElementException.class,
                () -> this.sockService.expenseSock(payload));
        //then
        assertEquals("Носков с таким цветом и содержанием хлопка на складе не найдено",exception.getMessage());
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void getAllSocksWithoutPayload_ReturnListAllSocks() {
        //given
        FilterSocksPayload payload = null;
        doReturn(List.of(new SockEntity("blue", 70d, 50d),
                new SockEntity("red", 50d, 25d)))
                .when(this.sockRepository).findAll();
        //when
        var result = this.sockService.getAllSocks(payload);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(List.of(new SockEntity("blue", 70d, 50d),
                new SockEntity("red", 50d, 25d)), result);
        verify(this.sockRepository).findAll();
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void getAllSocksWithPayload_ReturnFilteredListAllSocks() {
        //given
        var payload = new FilterSocksPayload("blue", null,null,null,
                null,null,null);
        Specification<SockEntity> specification =
                Specification.where((root, query, cb) -> cb.equal(root.get("color"), payload.sockColor()));
        doReturn(specification).when(this.builder).build(payload);
        var spec = this.builder.build(payload);
        doReturn(List.of(new SockEntity("blue", 70d, 50d)))
                .when(this.sockRepository).findAll(spec);
        //when
        var result = this.sockService.getAllSocks(payload);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(List.of(new SockEntity("blue", 70d, 50d)), result);
        verify(this.sockRepository).findAll(spec);
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void putSockWhenSockIsNotPresent_ThrowNoSuchElementException() {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        Integer id = 1;
        Optional<SockEntity> optionalSock = Optional.empty();
        doReturn(optionalSock)
                .when(this.sockRepository).findById(id);
        //when
        var exception = assertThrows(NoSuchElementException.class,
                () -> this.sockService.putSock(id,payload));
        //then
        assertEquals("Носков с таким цветом и содержанием хлопка на складе не найдено",exception.getMessage());
        verifyNoMoreInteractions(this.sockRepository);
    }

    @Test
    void putSockWhenSockIsPresent_ReturnUpdatedSockEntity() {
        //given
        var payload = new SockPayload("blue", 70d, 50d);
        Integer id = 1;
        Optional<SockEntity> optionalSock = Optional.of(new SockEntity("red", 50d, 25d));
        doReturn(optionalSock)
                .when(this.sockRepository).findById(id);
        var replacedSock = replaceSock(optionalSock.get(), payload);
        doReturn(replacedSock)
                .when(this.sockRepository).save(replacedSock);
        //when
        var result = this.sockService.putSock(id, payload);
        //then
        assertNotNull(result);
        assertEquals(new SockEntity("blue", 70d, 50d), result);
        verify(this.sockRepository).findById(id);
        verifyNoMoreInteractions(this.sockRepository);
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