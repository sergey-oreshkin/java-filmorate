package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc

public class UserControllerTest {

    private static User validUser;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void init() {
        validUser = new User(
                1,
                "email@mail.ru",
                "login",
                "name",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
    }

    @Test
    void test1_createValidUserResponseShouldBeOk() throws Exception {
        validUser.setId(validUser.getId() + 1);
        String body = mapper.writeValueAsString(validUser);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test3_updateValidUserResponseShouldBeOk() throws Exception {
        String body = mapper.writeValueAsString(validUser);
        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @MethodSource("invalidUsersSource")
    @ParameterizedTest(name = "{0}")
    void test5_createInvalidUserResponseShouldBeBadRequest(String name, User user) throws Exception {
        String body = mapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    private static Stream<Arguments> invalidUsersSource() {
        int i = 0;
        return Stream.of(
                Arguments.of("Empty email",
                        new User(
                                ++i,
                                "",
                                "login",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Bad email",
                        new User(
                                ++i,
                                "@mail.ru",
                                "login",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Empty login",
                        new User(
                                ++i,
                                "mail@mail.ru",
                                "",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Login with spaces",
                        new User(
                                ++i,
                                "mail@mail.ru",
                                "lo gin",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Birthday in the future",
                        new User(
                                ++i,
                                "mail@mail.ru",
                                "login",
                                "name",
                                LocalDate.of(2079, Month.SEPTEMBER, 17)
                        ))
        );
    }
}
