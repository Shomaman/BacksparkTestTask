package com.example.Backspark.TestTask.controller.payload;

public record FilterSocksPayload(String sockColor, String operation, Double betweenFrom, Double betweenTo,
                                 Double cotton, String sortField, String sortType) {}
