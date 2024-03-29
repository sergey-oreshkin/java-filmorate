package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
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
    public User create(@Valid @RequestBody User user) {
        if (user.getId() != 0) {
            throw new ValidationException("User id should be 0 for new user");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userService.update(user);
    }

    /**
     * @param id объекта пользователя, которого удалют
     * @author Grigory-PC
     * <p>
     * Удаляет пользователя из таблицы
     */
    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable long id) {
        return userService.delete(id);
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

    /**
     * Эндпоинт для получения списка рекомендованных фильмов для конкретного юзера
     *
     * @param id - идентификатор юзера для которого готовятся рекомендации
     * @return List<Film> - список рекомендованных фильмов основанный на коллаборативной фильтрации
     * @author sergey-oreshkin
     */
    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable long id) {
        return userService.getRecommendation(id);
    }
}

