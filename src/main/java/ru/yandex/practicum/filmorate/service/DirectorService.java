/**
 * Сервис для взаимодействия c хранилищем DirectorStorage
 *
 * @author Vladimir Arkhipenko
 */
package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.directorstorage.DirectorStorage;

@Service
public class DirectorService extends AbstractService<Director> {

    public DirectorService(DirectorStorage directorStorage) {
        super(directorStorage);
    }
}
