package ru.yandex.practicum.filmorate.model;

import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author aitski (Leonid Kvan)
 * Базовый класс с полями по функции "Отзывы"
 */

@Value
public class Review {

    @NonFinal
    @Setter
    long id;

    @NotBlank
    @Size(max = 3000)
    String content;

    @NonNull
    Boolean isPositive;

    @NonNull
    Long userId;

    @NonNull
    Long filmId;

    @NonFinal
    @Setter
    int useful;

}
