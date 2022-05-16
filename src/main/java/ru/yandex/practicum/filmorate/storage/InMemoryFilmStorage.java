package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    int nextId = 0;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        int id = getNextId();
        film.setId(id);
        return films.put(id, film);
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            return films.put(film.getId(), film);
        }
        throw new ValidationException("Update failed. Film with id=" + film.getId() + " does not exist.");
    }

    private int getNextId() {
        return ++nextId;
    }
}
