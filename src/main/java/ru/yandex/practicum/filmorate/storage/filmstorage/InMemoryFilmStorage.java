package ru.yandex.practicum.filmorate.storage.filmstorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortParam;

import java.util.*;
import java.util.stream.Collectors;

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
                .sorted(Comparator.comparing(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * @author Grigory-PC
     * <p>
     * Поиск 'by' по режиссеру или названию фильма в мапе на основании введенных символов в 'query'
     * Метод не реализован ввиду ненадобности
     */
    @Override
    public List<Film> search(String query, String by) {
        return null;
    }

    /**
     * Удаление фильма из мапы
     * Метод не реализован ввиду ненадобности
     */
    @Override
    public Film delete(long filmId) {
        return findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " does not exist"));
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
     *
     * @author Vladimir Arlhipenko
     */
    @Override
    public List<Film> getDirectorFilms(long id, SortParam sortBy) { // TODO
        return null;
    }

    private long getNextId() {
        return ++nextId;
    }
}
