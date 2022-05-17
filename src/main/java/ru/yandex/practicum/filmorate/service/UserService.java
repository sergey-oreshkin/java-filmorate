package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
        return user;
    }

    public List<User> getFriends(long id) {
        User user = userStorage.getById(id);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.getById(userId);
        User other = userStorage.getById(otherId);
        Set<Long> friends = new HashSet<>(user.getFriends());
        friends.retainAll(other.getFriends());
        return friends.stream().
                map(userStorage::getById)
                .collect(Collectors.toList());
    }
}
