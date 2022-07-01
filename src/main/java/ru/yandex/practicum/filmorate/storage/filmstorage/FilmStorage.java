package ru.yandex.practicum.filmorate.storage.filmstorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAll();

    Film create(Film f);

    Film update(Film f);

    Optional<Film> findById(long id);

    List<Film> getTop(int count);

    List<Film> search(String query, String by);

    void clear();
}
