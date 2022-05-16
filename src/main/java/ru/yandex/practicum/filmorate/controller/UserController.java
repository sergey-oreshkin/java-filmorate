package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public List<User> users() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @NotNull @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @NotNull @RequestBody User user) {
        return userService.update(user);
    }
}
