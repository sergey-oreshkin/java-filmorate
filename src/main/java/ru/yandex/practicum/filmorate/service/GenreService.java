package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAll(){
        return genreStorage.getAll();
    }

    public Genre getById(int id){
        Optional<Genre> genre = genreStorage.findById(id);
        if (genre.isPresent()){
            return genre.get();
        }
        throw new NotFoundException("Genre with id=" + id + " not found");
    }
}
