/**
 * Абстрактный класс сервисов. Содержит реализацию методов
 * getAll, create, update, getById, delete
 *
 * @author rs-popov
 */
package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public abstract class AbstractService<T> {

    private final Storage<T> storage;

    public AbstractService(Storage<T> storage) {
        this.storage = storage;
    }

    public List<T> getAll() {
        return storage.getAll();
    }

    public T create(T data) {
        //validate(data);
        storage.create(data);
        return data;
    }

    public T update(T data) {
        //validate(data);
        return storage.update(data);
    }

    public T getById(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Entity with id=" + id + " not found"));
    }

    public T delete(long id) {
        return storage.delete(id);
    }

}
