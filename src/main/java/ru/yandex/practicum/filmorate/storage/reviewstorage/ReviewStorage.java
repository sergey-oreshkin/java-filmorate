package ru.yandex.practicum.filmorate.storage.reviewstorage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);
    Review update(Review review);
    Review delete (long id);
    Optional<Review> findById(long id);

    List<Review> getReviewsByIdLimited (long filmId, int count);
    void updateLike (int useful, long reviewId);

}
