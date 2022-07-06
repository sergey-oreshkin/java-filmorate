/**
 * Интерфейс для работы с хранилищем режиссеров
 *
 * @author Vladimir Arkhipenko
 */
package ru.yandex.practicum.filmorate.storage.directorstorage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getAll();

    Director create(Director director);

    Director update(Director director);

    Optional<Director> findById(long id);

    Director deleteById(long directorId);
}
