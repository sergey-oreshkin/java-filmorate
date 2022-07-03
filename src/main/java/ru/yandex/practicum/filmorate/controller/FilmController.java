package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    private final FilmService filmService;

    @GetMapping
    public List<Film> films() {
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @NotNull @RequestBody Film film) {
        if (film.getId() != 0) {
            throw new ValidationException("Film id should be 0 for new film");
        }
        if (isDateValid(film)) {
            return filmService.create(film);
        }
        throw new ValidationException(
                "The date cannot be earlier than " +
                        EARLIEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }

    @PutMapping
    public Film update(@Valid @NotNull @RequestBody Film film) {
        if (isDateValid(film)) {
            return filmService.update(film);
        }
        throw new ValidationException(
                "The date cannot be earlier than " +
                        EARLIEST_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }

    @GetMapping("{id}")
    public Film getById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Film setLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.setLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

    /**
     * Эндпоинт для получения списка фильмов режиссера, отсортированные по лайкам или году релиза
     * @author Vladimir Arlhipenko
     * @param directorId - идентификатор режиссера по которому готовится список фильмов
     * @param sortBy - выбираемый тип сортировки (допустимы значения year(по году релиза), likes(по количеству лайков))
     *               default значение "likes"
     * @return List<Film> - список фильмов
     */
    @GetMapping("director/{directorId}")
    public List<Film> getDirectorFilms (@PathVariable long directorId,
                                        @RequestParam(defaultValue = "likes") String sortBy) {
        sortBy = sortBy.trim();
        if (sortBy.equals("year") || sortBy.equals("likes")) {
            return filmService.getDirectorFilms(directorId, sortBy);
        }
        throw new ValidationException("Sorting can't be " +
                sortBy + ". The following values are supported (year,likes)");
    }

    private boolean isDateValid(Film film) {
        return film.getReleaseDate().isAfter(EARLIEST_DATE);
    }
}
