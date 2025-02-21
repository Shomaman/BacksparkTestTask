package com.example.Backspark.TestTask.controller.payload;

import javax.validation.constraints.*;

public record SockPayload(
        @NotNull(message = "цвет должен быть указан")
        @Size(min = 3, max = 200, message = "название цвета должно быть от {min} до {max} символов")
        String sockColor,
        @NotNull(message = "процент содержания хлопка должен быть указан")
        @Min(message = "процент содержания хлопка должен быть больше 0",value = 0)
        @Max(message = "процент содержания хлопка не может быть больше 100",value = 100)
        Double cotton,
        @NotNull(message = "количество должно быть указано")
        @Positive(message = "количество должно быть больше 0")
        Double quantity) {
}
