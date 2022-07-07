package ru.yandex.practicum.filmorate.storage.userstorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
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
    public Optional<User> findById(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }

    /**
     * @author Grigory-PC
     * <p>
     * Удаление пользователя из мапы
     * Метод не реализован ввиду ненадобности
     */
    @Override
    public User delete(long userId) {
        return findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " does not exist"));
    }

    @Override
    public void clear() {
        users.clear();
        nextId = 0;
    }

    @Override
    public Map<Long, Map<Long, Integer>> getLikesMatrix() {
        return null; // TODO throw MethodNotImplemented
    }

    private long getNextId() {
        return ++nextId;
    }
}
