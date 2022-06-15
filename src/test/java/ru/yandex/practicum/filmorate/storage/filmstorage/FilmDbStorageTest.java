package ru.yandex.practicum.filmorate.storage.filmstorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
class FilmDbStorageTest {

    @Autowired
    private FilmStorage storage;

    @Test
    void getAll() {

        assertFalse(storage.getAll().isEmpty());
    }

    @Test
    void create() {
        Film film = new Film(
                0,
                "film name",
                "film desc",
                new Mpa(1),
                LocalDate.of(1900, Month.JANUARY, 5),
                200,
                new HashSet<>(),
                new HashSet<>()
        );

        assertTrue(storage.create(film).getId() != 0);

        assertThrows(ValidationException.class, () -> storage.create(film));
    }

    @Test
    void update() {
        Film film = new Film(
                1,
                "updated",
                "film desc",
                new Mpa(1),
                LocalDate.of(1900, Month.JANUARY, 5),
                200,
                new HashSet<>(),
                new HashSet<>()
        );

        assertTrue(storage.update(film).getName().equals("updated"));

        film.setId(100L);

        assertThrows(NotFoundException.class, () -> storage.update(film));
    }

    @Test
    void getById() {

        assertTrue(storage.findById(1L).isPresent());

        assertFalse(storage.findById(100L).isPresent());
    }

    @Test
    void getTop() {
        List<Film> films = storage.getTop(1);

        assertTrue(films.size() == 1
                && films.get(0).getLikes().size() == 2);
    }
}