package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.eventstorage.EventStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewstorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.List;

    /**
     * @author aitski (Leonid Kvan)
     * Класс для реализации бизнес-логики по функции "Отзывы"
     * создает, обновляет, удаляет, находит по айди,
     * выдает список всех отзывов с указанным лимитом (10 по умолчанию)
     * и по указанному фильму (по умолчанию - все фильмы),
     * с сортировкой по кол-ву лайков
     * добавляет/удаляет лайки/дислайки с (де)инкрементацией
     * поля useful
     */

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final EventStorage eventStorage;

    public Review create(Review review) {
        validateFilmId(review.getFilmId());
        validateUserId(review.getUserId());
        Review created = reviewStorage.create(review);
        eventStorage.create(Event.builder()
                .userId(created.getUserId())
                .eventType("REVIEW")
                .operation("ADD")
                .entityId(created.getId())
                .build());
        return created;
    }

    public Review update(Review review) {
        validateFilmId(review.getFilmId());
        validateUserId(review.getUserId());
        eventStorage.create(Event.builder()
                .userId(findById(review.getId()).getUserId())
                .eventType("REVIEW")
                .operation("UPDATE")
                .entityId(review.getId())
                .build());
        return reviewStorage.update(review);
    }

    public void delete(long id) {
        eventStorage.create(Event.builder()
                .userId(findById(id).getUserId())
                .eventType("REVIEW")
                .operation("REMOVE")
                .entityId(id)
                .build());
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
