package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {

    List<T> getAll();

    T create(T data);

    T update(T data);

    Optional<T> findById(long id);

    T delete(long Id);

}
