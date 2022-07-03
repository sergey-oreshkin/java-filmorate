package ru.yandex.practicum.filmorate.storage.reviewstorage;

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
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author aitski (Leonid Kvan)
 * Класс для связи с базой данных по функции "Отзывы"
 */

@Component
@Primary
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {

        if (findById(review.getId()).isPresent()) {
            throw new ValidationException("Review with id=" + review.getId() + " already exist");
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("reviews").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("userId", review.getUserId())
                .addValue("filmId", review.getFilmId())
                .addValue("useful", 0);

        Number num = jdbcInsert.executeAndReturnKey(parameters);
        review.setId(num.longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        validateId(review.getId());
        String sql = "update reviews set " +
                "content=?, " +
                "isPositive=? " +
                "where id=?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        return review;
    }

    @Override
    public Review delete(long id) {
        Review review = findById(id)
                .orElseThrow(()->new NotFoundException("Review with id=" + id + " does not exist"));
        String sql = "delete from reviews where id=?";
        jdbcTemplate.update(sql, id);
        return review;
    }

    @Override
    public Optional<Review> findById(long id) {
        String sql = "select * from reviews where id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject
                    (sql, this::mapRowToReview, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsByIdLimited(long filmId, int count) {

        if (filmId == 0) {
            String sql = "select * from reviews " +
                    "order by (useful) desc limit ?;";
            return jdbcTemplate.query(sql, this::mapRowToReview, count);
        }

        String sql1 = "select * from reviews " +
                "where filmId=? order by (useful) desc limit ?;";
        return jdbcTemplate.query(sql1, this::mapRowToReview, filmId, count);
    }

    @Override
    public void updateLike(int useful, long reviewId) {
        validateId(reviewId);
        String sql = "update reviews set " +
                "useful=? " +
                "where id = ?;";
        jdbcTemplate.update(sql, useful, reviewId);
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) {
            throw new NotFoundException("review not found");
        }
        Review review = new Review(
                rs.getLong("id"),
                rs.getString("content"),
                rs.getBoolean("isPositive"),
                rs.getLong("userId"),
                rs.getLong("filmId"),
                rs.getInt("useful")
        );
        return review;
    }

    private void validateId(long id) {
        if (findById(id).isEmpty()) {
            throw new NotFoundException("Review with id=" + id + " does not exist");
        }
    }
}
