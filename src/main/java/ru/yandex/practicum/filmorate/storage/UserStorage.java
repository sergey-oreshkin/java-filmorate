package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User create(User u);

    User update(User u);

    User getById(long id);

    void clear();
}
