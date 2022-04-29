package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final int MAX_DESC_LENGTH = 200;
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Set<Film> films = new HashSet<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films);
    }

    @PostMapping
    public void addFilm(@RequestBody Film film) {
        if (films.contains(film)) {
            log.warn("Unable to add. Film is already exist.");
            throw new ValidationException("Unable to add. Film is already exist.");
        }
        if (!validate(film)) {
            log.warn("Invalid film properties {}", film);
            throw new ValidationException("Invalid film properties");
        }
        films.add(film);
        log.info("Film added successful");
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {
        if (!validate(film)) {
            log.warn("Invalid film properties {}", film);
            throw new ValidationException("Invalid film properties");
        }
        films.add(film);
        log.info("Film added successful");
    }

    private boolean validate(Film film){
        return !film.getTitle().isEmpty()
                && film.getDescription().length() < MAX_DESC_LENGTH
                && LocalDate.parse(film.getDate(), formatter).isAfter(EARLIEST_DATE)
                && film.getDuration() > 0;
    }
}
