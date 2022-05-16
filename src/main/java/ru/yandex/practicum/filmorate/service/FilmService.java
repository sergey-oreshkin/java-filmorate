package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    private final FilmStorage storage;

    public List<Film> getAll() {
        return storage.getAll();
    }

    public Film create(Film film) {
        if (film.getId() != 0) {
            throw new ValidationException("Film id should be 0 for new film");
        }
        if (isDateValid(film)) {
            return storage.create(film);
        }
        throw new ValidationException(
                "The date cannot be earlier than " +
                        EARLIEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }

    public Film update(Film film) {
        if (isDateValid(film)) {
            return storage.update(film);
        }
        throw new ValidationException(
                "The date cannot be earlier than " +
                        EARLIEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }

    private boolean isDateValid(Film film) {
        return film.getReleaseDate().isAfter(EARLIEST_DATE);
    }
}
