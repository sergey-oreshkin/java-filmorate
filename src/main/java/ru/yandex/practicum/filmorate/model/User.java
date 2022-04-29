package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class User {
    private int id;
    @Email
    private String email;
    private String login;
    private String nicName;
    private String birthDate;
}
