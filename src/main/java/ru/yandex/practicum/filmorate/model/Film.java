package ru.yandex.practicum.filmorate.model;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Value
public class Film {

    @NonFinal
    @Setter
    long id;

    @NotBlank
    @Size(max = 50)
    String name;

    @NotBlank
    @Size(max = 200)
    String description;

    @NotNull
    Mpa mpa;

    LocalDate releaseDate;

    @Positive
    int duration;

    @NonFinal
    @Setter
    Set<Long> likes;

    @NonFinal
    @Setter
    Set<Genre> genres;

    /**
     * Множество для хранения режиссеров фильма
     *
     * @author Vladimir Arlhipenko
     */
    @NonFinal
    @Setter
    Set<Director> directors;

    @NonFinal
    @Setter
    int rate;
}
