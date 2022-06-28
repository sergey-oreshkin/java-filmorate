package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

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

    public List<Film> getRecommendation(long id) {
        getById(id); //validate user
        Map<Long, Map<Long, Integer>> matrix = userStorage.getLikesMatrix();
        Map<Long, Map<Long, Integer>> diffMatrix = new HashMap<>();
        Map<Long, Integer> freqMatrix = new HashMap<>();
        Map<Long, Integer> currentLikes = matrix.get(id);
        Set<Long> recommendations = new HashSet<>();

        for (Map.Entry<Long, Map<Long, Integer>> matrixEntry : matrix.entrySet()) {
            if (matrixEntry.getKey() == id) continue;
            diffMatrix.put(matrixEntry.getKey(), new HashMap<>());
            freqMatrix.put(matrixEntry.getKey(), 0);
            for (Map.Entry<Long, Integer> likesEntry : matrixEntry.getValue().entrySet()) {
                int diff = likesEntry.getValue() * currentLikes.get(likesEntry.getKey());
                diffMatrix.get(matrixEntry.getKey()).put(likesEntry.getKey(), diff);
                if (diff > 0) {
                    int oldFreq = freqMatrix.get(matrixEntry.getKey());
                    freqMatrix.put(matrixEntry.getKey(), oldFreq + 1);
                }
            }
        }
        freqMatrix.values().removeIf(v->v== 0);
        diffMatrix.keySet().removeIf(k->!freqMatrix.containsKey(k));

        diffMatrix.forEach((key, value) -> value.forEach((k,v)->{
            if (v == 0 && matrix.get(key).get(k) == 1){
                recommendations.add(k);
            }
        }));

        return recommendations.stream()
                .map(filmStorage::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList()
                );
    }
}
