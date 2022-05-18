package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film setLike(long filmId, long userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId); //user id validation
        film.setLike(userId);
        filmStorage.update(film);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId); //user id validation
        film.deleteLike(userId);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getTop(count);
    }
}
