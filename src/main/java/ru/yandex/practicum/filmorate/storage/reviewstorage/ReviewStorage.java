package ru.yandex.practicum.filmorate.storage.reviewstorage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface ReviewStorage extends Storage<Review> {

    List<Review> getReviewsByIdLimited(long filmId, int count);

    void updateLike(int useful, long reviewId);

}
