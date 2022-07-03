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
        validateUserId(friendId);
        user.getFriends().add(friendId);
        return userStorage.update(user);
    }

    public User deleteFriend(long userId, long friendId) {
        User user = getById(userId);
        validateUserId(friendId);
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

    /**
     * @param id - идентификатор юзера для которого готовятся рекомендации
     * @return - List<Film> - список рекомендованных фильмов основанный на коллаборативной фильтрации
     * @author sergey-oreshkin
     */
    public List<Film> getRecommendation(long id) {
        validateUserId(id);
        Map<Long, Map<Long, Integer>> matrix = userStorage.getLikesMatrix();
        Map<Long, Map<Long, Integer>> diffMatrix = new HashMap<>();
        Map<Long, Integer> freqMatrix = new HashMap<>();
        Map<Long, Integer> currentLikes = matrix.remove(id);
        Set<Long> recommendations = new HashSet<>();

        matrix.forEach((userId, likes) -> {
            diffMatrix.put(userId, new HashMap<>());
            freqMatrix.put(userId, 0);
            likes.forEach((filmId, like) -> {
                int diff = like * currentLikes.get(filmId);
                diffMatrix.get(userId).put(filmId, diff);
                if (diff > 0) {
                    int oldFreq = freqMatrix.get(userId);
                    freqMatrix.put(userId, oldFreq + 1);
                }
            });
        });

        diffMatrix.keySet().removeIf(k -> freqMatrix.get(k) == 0);

        diffMatrix.forEach((userId, likes) -> likes.forEach((filmId, diffLike) -> {
            if (diffLike == 0 && matrix.get(userId).get(filmId) == 1) {
                recommendations.add(filmId);
            }
        }));
        return recommendations.stream()
                .map(filmStorage::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList()
                );
    }

    private void validateUserId(long id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));
    }
    
     /**
     * @author Grigory-PC
     * <p>
     * Удаление пользователя из таблицы
     */
    public boolean delete(long id) {
        return userStorage.delete(getById(id));
    }
}
