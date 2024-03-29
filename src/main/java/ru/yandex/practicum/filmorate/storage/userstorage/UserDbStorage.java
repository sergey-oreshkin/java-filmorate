package ru.yandex.practicum.filmorate.storage.userstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        String sql = "select * from users where isDelete=false";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        if (findById(user.getId()).isPresent()) {
            throw new ValidationException("User with id=" + user.getId() + "already exist");
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("users").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("isDelete", false);

        Number num = jdbcInsert.executeAndReturnKey(parameters);

        user.setId(num.longValue());
        return user;
    }

    @Override
    public User update(User user) {
        findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User with id=" + user.getId() + " not exist"));
        String sql = "update users set " +
                "email=?," +
                "login=?," +
                "name=?," +
                "birthday=? " +
                "where id=?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        updateFriends(user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "select * from users where id=? and isDelete=false";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * @author Grigory-PC
     * <p>
     * Удаление пользователя из таблицы users
     */
    @Override
    public User delete(long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " does not exist"));
        String sql = "UPDATE users SET isDelete = true WHERE id = ?";
        jdbcTemplate.update(sql, userId);
        return user;
    }

    @Override
    public void clear() {
        String sql = "delete from users";
        jdbcTemplate.update(sql);
    }

    /**
     * @return Map<Long, Map < Long, Integer>> ключи первой мапы id юзеров,
     * второй ключи - id фильмов, значения : 1 - есть лайк, 0 - нет лайка
     * @author sergey-oreshkin
     */
    @Override
    public Map<Long, Map<Long, Integer>> getLikesMatrix() {
        String sqlFilms = "select distinct film_id from likes";
        String sqlLikes = "select * from likes";
        Map<Long, Map<Long, Integer>> matrix = new HashMap<>();
        Map<Long, Integer> filmLikesTemplate = new HashMap<>();

        List<Long> allFilmsId = jdbcTemplate.query(sqlFilms, (rs, i) -> rs.getLong("film_id"));
        allFilmsId.forEach(id -> filmLikesTemplate.put(id, 0));

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlLikes);
        while (rs.next()) {
            long userId = rs.getLong("user_id");
            if (!matrix.containsKey(userId)) {
                matrix.put(userId, new HashMap<>(filmLikesTemplate));
            }
            matrix.get(userId).put(rs.getLong("film_id"), 1);
        }
        return matrix;
    }

    private void updateFriends(User user) {
        String deleteSql = "delete from friendship where user_id=?";
        String insertSql = "insert into friendship (user_id, friend_id) " +
                "values (?,?)";

        jdbcTemplate.update(deleteSql, user.getId());
        if (user.getFriends() != null) {
            user.getFriends().forEach(id -> jdbcTemplate.update(insertSql, user.getId(), id));
        }
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) {
            throw new NotFoundException("user not found");
        }
        User user = new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null
        );
        user.setFriends(getFriendsByUserId(user.getId()));
        return user;
    }

    private Set<Long> getFriendsByUserId(long id) {
        String sql = "select friend_id from friendship where user_id=?";
        return new HashSet<>(
                jdbcTemplate.query(sql, (rs, num) -> rs.getLong("friend_id"), id)
        );
    }
}
