package ru.yandex.practicum.filmorate.storage.userstorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getAll();

    User create(User u);

    User update(User u);

    Optional<User> findById(long id);

    void clear();
}
