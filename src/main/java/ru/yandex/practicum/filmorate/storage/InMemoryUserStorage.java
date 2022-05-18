package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private long nextId = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new NotFoundException("Update failed. User with id=" + user.getId() + " does not exist.");
    }

    @Override
    public User getById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException("User with id=" + id + " not found");
    }

    @Override
    public void clear() {
        users.clear();
        nextId = 0;
    }

    private long getNextId() {
        return ++nextId;
    }
}
