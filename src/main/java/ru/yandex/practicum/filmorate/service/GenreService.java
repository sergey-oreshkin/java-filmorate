package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genrestorage.GenreStorage;

@Component
public class GenreService extends AbstractService<Genre> {

    public GenreService(GenreStorage genreStorage) {
        super(genreStorage);
    }

}
