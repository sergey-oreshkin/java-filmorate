package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpastorage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getAll(){
        return mpaStorage.getAll();
    }

    public Mpa getById(int id){
        Optional<Mpa> mpa = mpaStorage.findById(id);
        if (mpa.isPresent()){
            return mpa.get();
        }
        throw new NotFoundException("MPA with id=" + id + " not found");
    }
}
