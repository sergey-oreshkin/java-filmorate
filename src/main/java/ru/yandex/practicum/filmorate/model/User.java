package ru.yandex.practicum.filmorate.model;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Value
public class User {

    @NonFinal
    @Setter
    long id;

    @Email
    @NotBlank
    @Size(max = 50)
    String email;

    @NotBlank
    @Pattern(regexp = "\\S*$")
    @Size(max = 50)
    String login;

    @NonFinal
    @Setter
    @Size(max = 50)
    String name;

    @Past
    LocalDate birthday;

    @NonFinal
    @Setter
    Set<Long> friends;
}
