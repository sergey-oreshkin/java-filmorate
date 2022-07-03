package ru.yandex.practicum.filmorate.feedAOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.eventstorage.EventStorage;
import ru.yandex.practicum.filmorate.storage.reviewstorage.ReviewStorage;

/**
 * @author rs-popov
 * Класс-аспект для реализации логики по добавлению событий при
 * создании (аннотация @CretingEvent), удалении (аннотация @RemovingEvent)
 * и обновлении (аннотация @UpdatingEvent)
 **/

@Aspect
@Component
public class EventAspect {
    private final EventStorage eventStorage;

    private final ReviewStorage reviewStorage;

    public EventAspect(EventStorage eventStorage, ReviewStorage reviewStorage) {
        this.eventStorage = eventStorage;
        this.reviewStorage = reviewStorage;
    }

    @Pointcut("@annotation(ru.yandex.practicum.filmorate.feedAOP.CreatingEvent)")
    public void createMethod() {
    }

    @AfterReturning(value = "createMethod()", returning = "returningValue")
    public void createAddEvent(JoinPoint joinPoint, Object returningValue) {
        if (returningValue != null) {
            Object o = returningValue;
            if (o instanceof Review) {
                Review review = (Review) o;
                addEvent(review.getUserId(), review.getId(), "REVIEW", "ADD");
            }
            if (o instanceof Film) {
                Film film = (Film) o;
                addEvent((long) joinPoint.getArgs()[1], film.getId(), "LIKE", "ADD");
            }
            if (o instanceof User) {
                User user = (User) o;
                addEvent(user.getId(), (long) joinPoint.getArgs()[1], "FRIEND", "ADD");
            }
        }
    }

    @Pointcut("@annotation(ru.yandex.practicum.filmorate.feedAOP.RemovingEvent)")
    public void removeMethod() {
    }

    @AfterReturning(value = "removeMethod()", returning = "returningValue")
    public void createRemoveEvent(JoinPoint joinPoint, Object returningValue) {
        if (returningValue != null) {
            Object o = returningValue;
            if (o instanceof Review) {
                Review review = (Review) o;
                addEvent(review.getUserId(), review.getId(), "REVIEW", "REMOVE");
            }
            if (o instanceof Film) {
                Film film = (Film) o;
                addEvent((long) joinPoint.getArgs()[1], film.getId(), "LIKE", "REMOVE");
            }
            if (o instanceof User) {
                User user = (User) o;
                addEvent(user.getId(), (long) joinPoint.getArgs()[1], "FRIEND", "REMOVE");
            }
        }
    }

    @Pointcut("@annotation(ru.yandex.practicum.filmorate.feedAOP.UpdatingEvent)")
    public void updateMethod() {
    }

    @Around(value = "updateMethod()")
    public Object createUpdateEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object o = joinPoint.getArgs()[0];
        if (o instanceof Review) {
            Review review = (Review) o;
            addEvent(reviewStorage.findById(review.getId()).get().getUserId(),
                    review.getId(), "REVIEW", "UPDATE");
        }
        return joinPoint.proceed();
    }

    private void addEvent(long userId, long entityId, String eventType, String operation) {
        eventStorage.create(Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .build());
    }
}