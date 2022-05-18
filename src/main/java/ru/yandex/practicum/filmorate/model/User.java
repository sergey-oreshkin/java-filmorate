package ru.yandex.practicum.filmorate.model;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
public class User {

    @NonFinal
    @Setter
    long id;

    @Email
    @NotBlank
    String email;

    @NotBlank
    @Pattern(regexp = "\\S*$")
    String login;

    @NonFinal
    @Setter
    String name;

    @Past LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

    public void addFriend(long id) {
        friends.add(id);
    }

    public void deleteFriend(long id) {
        friends.remove(id);
    }
}
