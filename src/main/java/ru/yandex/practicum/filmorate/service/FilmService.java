package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public Film setLike(long filmId, long userId) {
        Film film = validateAndGetFilm(filmId, userId);
        film.getLikes().add(userId);
        return filmStorage.update(film);
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = validateAndGetFilm(filmId, userId);
        film.getLikes().remove(userId);
        return filmStorage.update(film);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getTop(count);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(long id) {
        Optional<Film> film = filmStorage.findById(id);
        if (film.isPresent()) {
            return film.get();
        }
        throw new NotFoundException("Film with id=" + id + " not found");
    }

    private Film validateAndGetFilm(long filmId, long userId) {
        Optional<Film> f = filmStorage.findById(filmId);
        if (f.isEmpty()) {
            throw new NotFoundException("Film with id=" + filmId + " not found");
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        return f.get();
    }
}
