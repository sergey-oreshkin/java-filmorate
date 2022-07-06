package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpastorage.MpaStorage;

@Component
public class MpaService extends AbstractService<Mpa> {

    public MpaService(MpaStorage mpaStorage) {
        super(mpaStorage);
    }

}
