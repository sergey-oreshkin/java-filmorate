/**
 * Сервис для взаимодействия c хранилищем DirectorStorage
 *
 * @author Vladimir Arkhipenko
 */
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.directorstorage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public Director findById(long id) {
        return directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Director with id=" + id + " not found"));
    }

    public void deleteById(long id) {
        directorStorage.deleteById(id);
    }
}
