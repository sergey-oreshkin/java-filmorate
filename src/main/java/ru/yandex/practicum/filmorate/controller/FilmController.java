package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    private final LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @NotNull @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Film already exist.");
        }
        return addFilm(film);
    }

    @PutMapping
    public Film addOrUpdate(@Valid @NotNull @RequestBody Film film) {
        return addFilm(film);
    }

    private boolean validate(Film film) {
        if (film.getReleaseDate().isAfter(EARLIEST_DATE)) {
            return true;
        }
        return false;
    }

    private Film addFilm(Film film) {
        if (validate(film)) {
            films.put(film.getId(), film);
            log.info("Film added successful");
            return film;
        }
        throw new ValidationException("Release date can't be before " + EARLIEST_DATE);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        log.warn("Validation failed. " + ex.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
