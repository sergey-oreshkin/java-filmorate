package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
}
