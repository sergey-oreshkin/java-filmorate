package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    private final int invalidId = 100;

    private Film validFilm;
    private User validUser;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilmStorage filmStorage;

    @Autowired
    UserStorage userStorage;

    @BeforeEach
    void reset() {
        validFilm = new Film(
                0,
                "film name",
                "film desc",
                LocalDate.of(1900, Month.JANUARY, 5),
                200
        );
        filmStorage.clear();

        validUser = new User(
                0,
                "email@mail.ru",
                "login",
                "user",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
        userStorage.clear();
    }

    @Test
    void test_1_createValidFilmResponseShouldBeOk() throws Exception {
        validFilm.setId(0);
        String body = mapper.writeValueAsString(validFilm);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test_2_updateValidFilmResponseShouldBeOk() throws Exception {
        filmStorage.create(validFilm);
        validFilm.setId(1);
        String body = mapper.writeValueAsString(validFilm);

        this.mockMvc.perform(put("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @MethodSource("invalidFilmsSource")
    @ParameterizedTest(name = "{0}")
    void test_3_createInvalidFilmResponseShouldBeBadRequest(String name, Film film) throws Exception {
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void test_4_getFilmByIdTest() throws Exception {
        filmStorage.create(validFilm);

        this.mockMvc.perform(get(format("/films/%d", invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get(format("/films/%d", validFilm.getId())))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(validFilm)));
    }

    @Test
    void test_5_setLikeTest() throws Exception {
        userStorage.create(validUser);
        filmStorage.create(validFilm);

        this.mockMvc.perform(put(format("/films/%d/like/%d", invalidId, validUser.getId())))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(put(format("/films/%d/like/%d", validFilm.getId(), invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(put(format("/films/%d/like/%d", validFilm.getId(), validUser.getId())))
                .andExpect(status().isOk());

        assertEquals(1, filmStorage.getAll().get(0).rate());
    }

    @Test
    void test_6_deleteLikeTest() throws Exception {
        userStorage.create(validUser);
        filmStorage.create(validFilm);
        validFilm.setLike(validUser.getId());

        this.mockMvc.perform(delete(format("/films/%d/like/%d", invalidId, validUser.getId())))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(format("/films/%d/like/%d", validFilm.getId(), invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(format("/films/%d/like/%d", validFilm.getId(), validUser.getId())))
                .andExpect(status().isOk());

        assertEquals(0, filmStorage.getAll().get(0).rate());
    }

    @Test
    void test_7_getPopularTest() throws Exception {
        Film anotherFilm = new Film(
                0,
                "film name",
                "film desc",
                LocalDate.of(1900, Month.JANUARY, 5),
                200
        );
        userStorage.create(validUser);
        filmStorage.create(anotherFilm);
        filmStorage.create(validFilm);
        validFilm.setLike(validUser.getId());

        this.mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(validFilm, anotherFilm))));

        this.mockMvc.perform((get(format("/films/popular?count=%d", 1))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(validFilm))));
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
                                "a".repeat(202),
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
