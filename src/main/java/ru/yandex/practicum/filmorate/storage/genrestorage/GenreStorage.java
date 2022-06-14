package ru.yandex.practicum.filmorate.storage.genrestorage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> getAll();

    Optional<Genre> findById(int id);
}
