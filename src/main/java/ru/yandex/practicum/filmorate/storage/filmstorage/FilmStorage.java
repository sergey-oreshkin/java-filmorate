package ru.yandex.practicum.filmorate.storage.filmstorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {

    List<Film> getTop(int count);

    List<Film> search(String query, String by);

    List<Film> getCommonFilms(long userId, long friendId);

    void clear();

    /**
     * Метод для получения списка фильмов режиссера, отсортированные по лайкам(likes) или году релиза(year)
     *
     * @param id     - идентификатор режиссера по которому готовится список фильмов
     * @param sortBy - выбираемый тип сортировки (допустимы значения year(по году релиза), likes(по количеству лайков))
     *               default значение "likes"
     * @return List<Film> - список фильмов
     * @author Vladimir Arlhipenko
     */
    List<Film> getDirectorFilms(long id, String sortBy);
}
