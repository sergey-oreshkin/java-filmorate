package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
}
