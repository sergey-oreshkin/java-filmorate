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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    private static Film validFilm;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void init() {
        validFilm = new Film(
                1,
                "film name",
                "film desc",
                LocalDate.of(1900, Month.JANUARY, 5),
                200
        );
    }

    @Test
    void test2_createValidFilmResponseShouldBeOk() throws Exception {
        validFilm.setId(validFilm.getId() + 1);
        String body = mapper.writeValueAsString(validFilm);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test4_updateValidFilmResponseShouldBeOk() throws Exception {
        String body = mapper.writeValueAsString(validFilm);
        this.mockMvc.perform(put("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @MethodSource("invalidFilmsSource")
    @ParameterizedTest(name = "{0}")
    void test6_createInvalidUserResponseShouldBeBadRequest(String name, Film film) throws Exception {
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    private static Stream<Arguments> invalidFilmsSource() {
        int i = 0;
        return Stream.of(
                Arguments.of("Empty name",
                        new Film(
                                ++i,
                                "",
                                "film desc",
                                LocalDate.of(1999, Month.JANUARY, 5),
                                200
                        )),
                Arguments.of("Too long description",
                        new Film(
                                ++i,
                                "film",
                                "+1aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                                LocalDate.of(1999, Month.JANUARY, 5),
                                200
                        )),
                Arguments.of("Date before 28.12.1895",
                        new Film(
                                ++i,
                                "film",
                                "desc",
                                LocalDate.of(1895, Month.DECEMBER, 5),
                                200
                        )),
                Arguments.of("Negative duration",
                        new Film(
                                ++i,
                                "film",
                                "desc",
                                LocalDate.of(1895, Month.DECEMBER, 5),
                                -2
                        ))
        );
    }
}
