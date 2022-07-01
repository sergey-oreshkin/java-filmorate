package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;

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
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Film with id=" + id + " not found"));
    }

    /**
     * @author Grigory-PC
     * <p>
     * Поиск 'by' по режиссеру или названию фильма в таблице на основании введенных символов в 'query'
     */
    public List<Film> searchFilm(String query, String by) {
        return filmStorage.search(query, by);
    }

    private Film validateAndGetFilm(long filmId, long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " not found"));
    }
}
