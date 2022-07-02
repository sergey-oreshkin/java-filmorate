package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> users() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @NotNull @RequestBody User user) {
        if (user.getId() != 0) {
            throw new ValidationException("User id should be 0 for new user");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @NotNull @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userService.update(user);
    }

    @GetMapping("{id}")
    public User getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    /**
     * @param id - идентификатор пользователя
     * @return Возвращает ленту событий пользователя
     * @author rs-popov
     * Эндпойнт /users/{id}/feed [GET]
     */
    @GetMapping("{id}/feed")
    public List<Event> getFeed(@PathVariable long id) {
        return userService.getFeed(id);
    }
}
