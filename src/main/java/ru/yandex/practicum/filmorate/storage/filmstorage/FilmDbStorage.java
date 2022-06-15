package ru.yandex.practicum.filmorate.storage.filmstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {
        String sql = "select * from film F " +
                "left join rating R on R.id=F.rating_id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        if (findById(film.getId()).isPresent()) {
            throw new ValidationException("Film with id=" + film.getId() + "already exist");
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("film").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("rating_id", film.getMpa().getId())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration());

        Number num = jdbcInsert.executeAndReturnKey(parameters);

        film.setId(num.longValue());
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (findById(film.getId()).isEmpty()) {
            throw new NotFoundException("Film with id=" + film.getId() + " not found");
        }
        String sql = "update film set " +
                "name=?," +
                "description=?," +
                "rating_id=?," +
                "release_date=?," +
                "duration=? " +
                "where id=?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        updateGenres(film);
        updateLikes(film);
        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        String sql = "select * from film F " +
                "left join rating R on R.id=F.rating_id " +
                "where F.id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getTop(int count) {
        String sql = "select F.id  rate from film F " +
                "left join likes L on F.id=L.film_id " +
                "group by F.id " +
                "order by count(L.film_id) desc " +
                "limit ?";
        List<Integer> idList = jdbcTemplate.query(sql, rs -> {
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
            return ids;
        }, count);
        sql = "select * from film F " +
                "join rating R on R.id=F.rating_id " +
                "where F.id IN (" + String.join(",", Collections.nCopies(idList.size(), "?")) + ")";
        return jdbcTemplate.query(sql, this::mapRowToFilm, idList.toArray());
    }

    @Override
    public void clear() {
        String sql = "delete from film";
        jdbcTemplate.update(sql);
    }

    private void updateLikes(Film film) {
        String deleteSql = "delete from likes where film_id=?";
        String insertSql = "insert into likes (film_id, user_id) values(?,?)";

        jdbcTemplate.update(deleteSql, film.getId());

        if (film.getLikes() != null) {
            film.getLikes().forEach(id -> jdbcTemplate.update(insertSql, film.getId(), id));
        }
    }

    private void updateGenres(Film film) { //TODO update safely
        String deleteSql = "delete from film_genre where film_id=?";
        String insertSql = "insert into film_genre (film_id, genre_id) values(?,?)";

        jdbcTemplate.update(deleteSql, film.getId());
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(id -> jdbcTemplate.update(insertSql, film.getId(), id));
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) {
            throw new NotFoundException("film not found");
        }
        Film film = new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                new Mpa(rs.getInt("rating_id"), rs.getString("rating")),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                null,
                null
        );
        film.setGenres(getGenresByFilmId(film.getId()));
        film.setLikes(getLikesByFilmId(film.getId()));
        return film;
    }

    private Set<Genre> getGenresByFilmId(long id) {
        String sql = "select genre, genre_id from genre G " +
                "left join film_genre FG on FG.genre_id=G.id " +
                "where film_id=?";
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(
                sql,
                (rs, num) -> new Genre(rs.getInt("genre_id"), rs.getString("genre")),
                id)
        );
        return genres.isEmpty() ? null : genres;
    }

    private Set<Long> getLikesByFilmId(long id) {
        String sql = "select user_id from likes where film_id=?";
        return new HashSet<>(
                jdbcTemplate.query(sql, (rs, num) -> rs.getLong("user_id"), id)
        );
    }
}
