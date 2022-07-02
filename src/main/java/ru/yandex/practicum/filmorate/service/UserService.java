package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.eventstorage.EventStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    private final EventStorage eventStorage;

    public User addFriend(long userId, long friendId) {
        User user = getById(userId);
        getById(friendId); //validate friendId
        user.getFriends().add(friendId);
        eventStorage.create(Event.builder()
                .userId(userId)
                .eventType("FRIEND")
                .operation("ADD")
                .entityId(friendId)
                .build());
        return userStorage.update(user);
    }

    public User deleteFriend(long userId, long friendId) {
        User user = getById(userId);
        getById(friendId); //validate friendId
        user.getFriends().remove(friendId);
        eventStorage.create(Event.builder()
                .userId(userId)
                .eventType("FRIEND")
                .operation("REMOVE")
                .entityId(friendId)
                .build());
        return userStorage.update(user);
    }

    public List<User> getFriends(long id) {
        return getById(id).getFriends().stream()
                .flatMap(fId -> userStorage.findById(fId).stream())
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = getById(userId);
        User other = getById(otherId);
        Set<Long> friends = user.getFriends();
        friends.retainAll(other.getFriends());
        return friends.stream()
                .flatMap(fId -> userStorage.findById(fId).stream())
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
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));
    }

    /**
     * @param id - идентификатор пользователя
     * @return Возвращает список событий, совершенных пользователем с идентификатором id
     * @author rs-popov
     */
    public List<Event> getFeed(long id) {
        return eventStorage.getEventsByUserId(id);
    }
}
