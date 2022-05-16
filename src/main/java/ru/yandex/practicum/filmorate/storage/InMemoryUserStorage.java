package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    private int nextId = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        int id = getNextId();
        user.setId(id);
        return users.put(id, user);
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            return users.put(user.getId(), user);
        }
        throw new ValidationException("Update failed. User with id=" + user.getId() + " does not exist.");
    }

    private int getNextId() {
        return ++nextId;
    }
}
