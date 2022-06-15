package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpastorage.MpaStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }

    public Mpa getById(int id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA with id=" + id + " not found"));
    }
}
