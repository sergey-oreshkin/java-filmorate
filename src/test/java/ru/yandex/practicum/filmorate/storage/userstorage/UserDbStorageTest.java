package ru.yandex.practicum.filmorate.storage.userstorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class UserDbStorageTest {

    @Autowired
    private UserStorage storage;

    @Test
    void getAll() {

        assertFalse(storage.getAll().isEmpty());
    }

    @Test
    void create() {
        User user = new User(
                0,
                "email@mail.ru",
                "login",
                "user",
                LocalDate.of(1979, Month.SEPTEMBER, 17),
                new HashSet<>()
        );

        assertTrue(storage.create(user).getId() != 0);

        assertThrows(ValidationException.class, () -> storage.create(user));
    }

    @Test
    void update() {
        User user = new User(
                1,
                "email@mail.ru",
                "login",
                "updated",
                LocalDate.of(1979, Month.SEPTEMBER, 17),
                new HashSet<>()
        );

        assertTrue(storage.update(user).getName().equals("updated"));

        user.setId(100L);

        assertThrows(NotFoundException.class, () -> storage.update(user));
    }

    @Test
    void getById() {

        assertTrue(storage.findById(1L).isPresent());

        assertFalse(storage.findById(-1L).isPresent());
    }
}