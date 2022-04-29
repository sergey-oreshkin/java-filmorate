package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private String birthday;
}
