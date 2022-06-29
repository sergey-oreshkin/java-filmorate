package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewstorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review create(Review review) {
        validateFilmId(review.getFilmId());
        validateFilmId(review.getUserId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        validateFilmId(review.getFilmId());
        validateFilmId(review.getUserId());
        return reviewStorage.update(review);
    }

    public void delete(long id) {
        reviewStorage.delete(id);
    }

    public Review findById(long id) {
        return reviewStorage.findById(id)
                .orElseThrow(() -> new NotFoundException
                        ("Review with id=" + id + " not found"));
    }

    public List<Review> getReviewsByIdLimited(long filmId, int count) {
        return reviewStorage.getReviewsByIdLimited(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        validateUserId(userId);
        int useful = reviewStorage.findById(reviewId).get().getUseful();
        reviewStorage.updateLike(++useful, reviewId);

    }

    public void addDislike(long reviewId, long userId) {
        validateUserId(userId);
        int useful = reviewStorage.findById(reviewId).get().getUseful();
        reviewStorage.updateLike(--useful, reviewId);
    }

    public void deleteLike(long reviewId, long userId) {
        addDislike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        addLike(reviewId, userId);
    }

    private void validateFilmId(long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException
                        ("Film with id=" + filmId + " not found"));
    }

    private void validateUserId(long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException
                        ("User with id=" + userId + " not found"));
    }

}
