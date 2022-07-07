package ru.yandex.practicum.filmorate.storage.userstorage;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Map;

public interface UserStorage extends Storage<User> {

    void clear();

    Map<Long, Map<Long, Integer>> getLikesMatrix();
}
