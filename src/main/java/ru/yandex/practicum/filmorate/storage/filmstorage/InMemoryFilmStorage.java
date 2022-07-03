package ru.yandex.practicum.filmorate.storage.filmstorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private long nextId = 0;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        long id = getNextId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        throw new NotFoundException("Update failed. Film with id=" + film.getId() + " does not exist.");
    }

    @Override
    public Optional<Film> findById(long id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Film> getTop(int count) {
        if (count < 0) count = 0;
        return films.values().stream()
                .sorted(Comparator.comparing(Film::rate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * @author Grigory-PC
     * <p>
     * Удаление фильма из мапы
     * Метод не реализован ввиду ненадобности
     */
    @Override
    public boolean delete(Film film) {
        return false;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return null;
    }


    @Override
    public void clear() {
        nextId = 0;
        films.clear();
    }

    /**
     * Заглушка для метода
     * @author Vladimir Arlhipenko
     */
    @Override
    public List<Film> getDirectorFilms(long id, String sortBy) { // TODO
        return new ArrayList<>();
    }

    private long getNextId() {
        return ++nextId;
    }
}
