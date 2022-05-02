package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> users() {
        return users.values();
    }

    @PostMapping
    public User add(@Valid @NotNull @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("User already exist.");
        }
        return addUser(user);
    }

    @PutMapping
    public User addOrUpdate(@Valid @NotNull @RequestBody User user) {
        return addUser(user);
    }

    private boolean validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return !user.getLogin().contains(" ");
    }

    private User addUser(User user) {
        if (validate(user)) {
            users.put(user.getId(), user);
            log.info("User added successful");
            return user;
        }
        throw new ValidationException("Spaces in login is unacceptable");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        log.warn("Validation failed. " + ex.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
