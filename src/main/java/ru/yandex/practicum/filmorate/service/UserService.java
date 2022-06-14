package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addFriend(long userId, long friendId) {
        User user = getById(userId);
        getById(friendId); //validate friendId
        user.getFriends().add(friendId);
        return userStorage.update(user);
    }

    public User deleteFriend(long userId, long friendId) {
        User user = getById(userId);
        getById(friendId); //validate friendId
        user.getFriends().remove(friendId);
        return userStorage.update(user);
    }

    public List<User> getFriends(long id) {
        return getById(id).getFriends().stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = getById(userId);
        User other = getById(otherId);
        Set<Long> friends = user.getFriends();
        friends.retainAll(other.getFriends());
        return friends.stream().
                map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getById(long id) {
        Optional<User> u = userStorage.findById(id);
        if (u.isPresent()) {
            return u.get();
        }
        throw new NotFoundException("User with id=" + id + " not found");
    }

}
