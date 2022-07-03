package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author aitski (Leonid Kvan)
 * Класс-контроллер, принимающий http-запросы
 * и выдающий ответы по функции "Отзывы"
 */

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @NotNull @RequestBody Review review) {
        if (review.getId() != 0) {
            throw new ValidationException("Review id should be 0 for new review");
        }
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @NotNull @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        reviewService.delete(id);
    }

    @GetMapping("{id}")
    public Review findById(@PathVariable long id) {
        return reviewService.findById(id);
    }

    @GetMapping()
    public List<Review> getReviewsByIdLimited(
            @RequestParam(defaultValue = "0") long filmId,
            @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsByIdLimited(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(
            @PathVariable long id,
            @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislike(
            @PathVariable long id,
            @PathVariable long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(
            @PathVariable long id,
            @PathVariable long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(
            @PathVariable long id,
            @PathVariable long userId) {
        reviewService.deleteDislike(id, userId);
    }
}
