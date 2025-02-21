package com.example.Backspark.TestTask.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SockControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void addSockWithValidPayloadAndSocksIsNotPresent_ReturnsSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":70,
                        "quantity":50
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id":4,
                                "sockColor": "black",
                                "cotton":70,
                                "quantity":50
                                }
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void addSockWithValidPayloadAndSocksIsPresent_ReturnsSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":50,
                        "quantity":30
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id":2,
                                "sockColor": "black",
                                "cotton":50,
                                "quantity":100
                                }
                                """)
                );
    }

    @Test
    void addSockWithInvalidPayload_ThrowsBindException() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": null,
                        "cotton":-1,
                        "quantity":0
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  "цвет должен быть указан",
                                  "процент содержания хлопка должен быть больше 0",
                                  "количество должно быть больше 0"
                                 ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void expenseSockWithValidPayloadAndSocksIsPresentButNotEnough_ThrowsNotEnoughSocksException() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/outcome")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":50,
                        "quantity":150
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                ["Нехватка носков на складе"]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void expenseSockWithValidPayloadAndSocksIsPresentAndEnough_ReturnsSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/outcome")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":50,
                        "quantity":35
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                     "id":2,
                                     "sockColor": "black",
                                     "cotton":50,
                                     "quantity":35
                                }
                                """)
                );
    }

    @Test
    void expenseSockWithInvalidPayload_ThrowsBindException() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/outcome")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "",
                        "cotton":200,
                        "quantity":null
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  "название цвета должно быть от 3 до 50 символов",
                                  "процент содержания хлопка не может быть больше 100",
                                  "количество должно быть указано"
                                 ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void expenseSockWithValidPayloadButSocksIsNotPresent_ThrowsNoSuchElementException() throws Exception {
        //given
        var request = MockMvcRequestBuilders.post("/api/socks/outcome")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":60,
                        "quantity":50
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [ "Носков с таким цветом и содержанием хлопка на складе не найдено"]
                               """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void putSockWithValidPayloadAndPresentSockEntity_ReturnsSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.put("/api/socks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":50,
                        "quantity":50
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id":1,
                                "sockColor":"black",
                                "cotton":50,
                                "quantity":50
                                }
                                """)
                );
    }

    @Test
    void putSockWithInvalidPayload_ThrowsBindException() throws Exception {
        //given
        var request = MockMvcRequestBuilders.put("/api/socks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "",
                        "cotton":200,
                        "quantity":null
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  "название цвета должно быть от 3 до 50 символов",
                                  "процент содержания хлопка не может быть больше 100",
                                  "количество должно быть указано"
                                 ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void putSockWithValidPayloadButNotPresentSockEntity_ThrowsNoSuchElementException() throws Exception {
        //given
        var request = MockMvcRequestBuilders.put("/api/socks/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black",
                        "cotton":50,
                        "quantity":50
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [ "Носков с таким цветом и содержанием хлопка на складе не найдено"]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithoutFilters_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":1,
                                     "sockColor":"red",
                                     "cotton":70,
                                     "quantity":50
                                    },
                                    {
                                    "id":2,
                                     "sockColor":"black",
                                     "cotton":50,
                                     "quantity":70
                                    },
                                    {
                                    "id":3,
                                     "sockColor":"blue",
                                     "cotton":60,
                                     "quantity":60
                                    }
                                ]
                                """)
                );
    }

    @Test
    @Sql("/sql/schema.sql")
    void getAllSocksWithoutSocks_ReturnsEmptyList() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                []
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithFilterColor_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sockColor": "black"
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":2,
                                     "sockColor":"black",
                                     "cotton":50,
                                     "quantity":70
                                    }
                                ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithFilterCotton_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "cotton": "60"
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":3,
                                     "sockColor":"blue",
                                     "cotton":60,
                                     "quantity":60
                                    }
                                ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithFilterComparison_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "operation": "moreThan",
                        "cotton": "50"
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":1,
                                     "sockColor":"red",
                                     "cotton":70,
                                     "quantity":50
                                    },
                                    {
                                    "id":3,
                                     "sockColor":"blue",
                                     "cotton":60,
                                     "quantity":60
                                    }
                                ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithFilterRange_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "betweenFrom": 55,
                        "betweenTo": 66
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":3,
                                     "sockColor":"blue",
                                     "cotton":60,
                                     "quantity":60
                                    }
                                ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithFilterSortColorDesc_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sortField": "sockColor",
                        "sortType":"desc"
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":2,
                                     "sockColor":"black",
                                     "cotton":50,
                                     "quantity":70
                                    },
                                    {
                                    "id":3,
                                     "sockColor":"blue",
                                     "cotton":60,
                                     "quantity":60
                                    }, {
                                    "id":1,
                                     "sockColor":"red",
                                     "cotton":70,
                                     "quantity":50
                                    }
                                ]
                                """)
                );
    }

    @Test
    @Sql({"/sql/schema.sql", "/sql/data.sql"})
    void getAllSocksWithFilterSortCottonAsc_ReturnsListSockEntity() throws Exception {
        //given
        var request = MockMvcRequestBuilders.get("/api/socks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "sortField": "cotton",
                        "sortType":"asc"
                        }
                        """);
        //when
        mockMvc.perform(request)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                    "id":2,
                                     "sockColor":"black",
                                     "cotton":50,
                                     "quantity":70
                                    },
                                    {
                                    "id":3,
                                     "sockColor":"blue",
                                     "cotton":60,
                                     "quantity":60
                                    }, {
                                    "id":1,
                                     "sockColor":"red",
                                     "cotton":70,
                                     "quantity":50
                                    }
                                ]
                                """)
                );
    }

}
