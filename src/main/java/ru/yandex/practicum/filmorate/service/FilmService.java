package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.feedAOP.CreatingEvent;
import ru.yandex.practicum.filmorate.feedAOP.RemovingEvent;
import ru.yandex.practicum.filmorate.storage.directorstorage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final DirectorStorage directorStorage;

    @CreatingEvent
    public Film setLike(long filmId, long userId) {
        Film film = validateAndGetFilm(filmId, userId);
        film.getLikes().add(userId);
        return filmStorage.update(film);
    }

    @RemovingEvent
    public Film deleteLike(long filmId, long userId) {
        Film film = validateAndGetFilm(filmId, userId);
        film.getLikes().remove(userId);
        return filmStorage.update(film);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getTop(count);
    }


    public List<Film> getCommonFilms(long userId, long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    /**
     * Возвращает список из первых count фильмов по количеству лайков. Если в параметрах
     * передано значение идентификатора жанра, список отфильтровывается, и в итоговом списке
     * остаются только фильмы, имеющие в списке жанров пункт, который соответствует переданному идентификатору.
     * Если в параметрах передается год, то список отфильтровывается таким образом, что в итоговом списке
     * остаются только фильмы, имеющие год выпуска, который соответствует переданному параметру.
     *
     * @param count   - размер списка фильмов
     * @param genreId - идентификатор жанра
     * @param year    - год выпуска
     * @return список List<Film> топ фильмов по количеству лайков, отфильтрованный по жанру и по году
     */
    public List<Film> getPopularFiltered(int count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Film> filmList = filmStorage.getTop(count);
        if (genreId.isPresent()) {
            filmList = filmList.stream()
                    .filter(film -> film.getGenres() != null)
                    .filter(film -> film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId.get()))
                    .collect(Collectors.toList());
        }
        if (year.isPresent()) {
            filmList = filmList.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year.get())
                    .collect(Collectors.toList());
        }
        return filmList;

    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Film with id=" + id + " not found"));
    }

    /**
     * @author Grigory-PC
     * <p>
     * Поиск 'by' по режиссеру или названию фильма в таблице на основании введенных символов в 'query'
     */
    public List<Film> searchFilm(String query, String by) {
        return filmStorage.search(query, by);
    }

    /**
     * Метод для получения списка фильмов режиссера, отсортированные по лайкам(likes) или году релиза(year)
     * так же в методе проверяется корректность переданного directorId
     *
     * @param directorId - идентификатор режиссера по которому готовится список фильмов
     * @param sortBy     - выбираемый тип сортировки (допустимы значения year(по году релиза), likes(по количеству лайков))
     *                   default значение "likes"
     * @return List<Film> - список фильмов
     * @author Vladimir Arlhipenko
     */
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        directorStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Director with id=" + directorId + " not found"));
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    /**
     * @author Grigory-PC
     * <p>
     * Удаление фильма из таблицы
     */
    public boolean delete(long id) {
        return filmStorage.delete(getById(id));
    }

    private Film validateAndGetFilm(long filmId, long userId) {
        validateUserId(userId);

        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id=" + filmId + " not found"));
    }

    private void validateUserId(long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

}
