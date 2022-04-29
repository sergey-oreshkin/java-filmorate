package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Set<User> users = new HashSet<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping
    public List<User> users() {
        return new ArrayList<>(users);
    }

    @PostMapping
    public void addUser(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            log.warn("Unable to add. User is already exist.");
            throw new ValidationException("Unable to add. User is already exist.");
        }
        if (!validate(user)) {
            log.warn("Invalid user properties {}", user);
            throw new ValidationException("Invalid user properties");
        }
        users.add(user);
        log.info("User added successful");
    }

    @PutMapping
    public void updateUser(@Valid @RequestBody User user) {
        if (!validate(user)) {
            log.warn("Invalid user properties {}", user);
            throw new ValidationException("Invalid user properties");
        }
        users.add(user);
        log.info("User updated successful");
    }

    private boolean validate(User user) {
        if (user == null) return false;
        if (user.getName() == null || user.getName().isEmpty()){
            user.setName(user.getLogin());
        }
        return !user.getEmail().isEmpty()
                && user.getEmail().contains("@")
                && !user.getLogin().isEmpty()
                && !user.getLogin().contains(" ")
                && LocalDate.parse(user.getBirthday(),formatter).isBefore(LocalDate.now());
    }
}
