package ru.yandex.practicum.filmorate.storage.userstorage;

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
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User create(User u) {
        if (findById(u.getId()).isPresent()) {
            throw new ValidationException("User with id=" + u.getId() + "already exist");
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("users").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", u.getEmail())
                .addValue("login", u.getLogin())
                .addValue("name", u.getName())
                .addValue("birthday", u.getBirthday());

        Number num = jdbcInsert.executeAndReturnKey(parameters);

        u.setId(num.longValue());
        return u;
    }

    @Override
    public User update(User u) {
        if (findById(u.getId()).isEmpty()) {
            throw new NotFoundException("User with id=" + u.getId() + " not exist");
        }
        String sql = "update users set " +
                "email=?," +
                "login=?," +
                "name=?," +
                "birthday=? " +
                "where id=?";
        jdbcTemplate.update(sql,
                u.getEmail(),
                u.getLogin(),
                u.getName(),
                u.getBirthday(),
                u.getId());
        updateFriends(u);
        return u;
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "select * from users where id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void clear() {
        String sql = "delete from users";
        jdbcTemplate.update(sql);
    }

    private void updateFriends(User u) {
        String deleteSql = "delete from friendship where user_id=?";
        String insertSql = "insert into friendship (user_id, friend_id) " +
                "values (?,?)";

        jdbcTemplate.update(deleteSql, u.getId());
        if (u.getFriends() != null) {
            u.getFriends().forEach(id -> jdbcTemplate.update(insertSql, u.getId(), id));
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
