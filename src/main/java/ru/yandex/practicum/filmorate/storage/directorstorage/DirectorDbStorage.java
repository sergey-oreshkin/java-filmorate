/**
 * Имлиментация интерфейса DirectorStorage DAO класс для таблицы Directors(Режиссеры)
 *
 * @author Vladimir Arkhipenko
 */
package ru.yandex.practicum.filmorate.storage.directorstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAll() {
        String sql = "select * from directors";
        List<Director> allDirectors = new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToDirector));
        return allDirectors.isEmpty() ? new ArrayList<>() : allDirectors;
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("directors").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", director.getName());

        long directorId = jdbcInsert.executeAndReturnKey(parameters).longValue();

        director.setId(directorId);
        return director;
    }

    @Override
    public Director update(Director director) {
        findById(director.getId())
                .orElseThrow(() -> new NotFoundException("Director with id=" + director.getId() + " not found"));
        String sql = "update directors set " +
                "name=?" +
                "where id=?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public Optional<Director> findById(long id) {
        String sql = "select * from directors where id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToDirector, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Director delete(long directorId) {
        Director director = findById(directorId)
                .orElseThrow(() -> new NotFoundException("Director with id=" + directorId + " does not exist"));
        String sqlFilmDirector = "delete from film_director where director_id=?";
        String sqlDirectors = "delete from directors where id=?";
        jdbcTemplate.update(sqlFilmDirector, director.getId());
        jdbcTemplate.update(sqlDirectors, director.getId());
        return director;
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) {
            throw new NotFoundException("Director not found");
        }
        return new Director(rs.getLong("id"), rs.getString("name"));
    }
}
