package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.feedAOP.CreatingEvent;
import ru.yandex.practicum.filmorate.feedAOP.RemovingEvent;
import ru.yandex.practicum.filmorate.storage.eventstorage.EventStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractService<User>{

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final EventStorage eventStorage;

    public UserService(UserStorage userStorage, FilmStorage filmStorage, EventStorage eventStorage) {
        super(userStorage);
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    @CreatingEvent
    public User addFriend(long userId, long friendId) {
        User user = getById(userId);
        validateUserId(friendId);
        user.getFriends().add(friendId);
        return userStorage.update(user);
    }

    @RemovingEvent
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


    /**
     * @param id - идентификатор пользователя
     * @return Возвращает список событий, совершенных пользователем с идентификатором id
     * @author rs-popov
     */
    public List<Event> getFeed(long id) {
        return eventStorage.getEventsByUserId(id);
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

}
