package ru.yandex.practicum.filmorate.feedAOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
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
                addEvent(review.getUserId(), review.getId(), EventType.REVIEW, Operation.ADD);
            }
            if (o instanceof Film) {
                Film film = (Film) o;
                addEvent((long) joinPoint.getArgs()[1], film.getId(), EventType.LIKE, Operation.ADD);
            }
            if (o instanceof User) {
                User user = (User) o;
                addEvent(user.getId(), (long) joinPoint.getArgs()[1], EventType.FRIEND, Operation.ADD);
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
                addEvent(review.getUserId(), review.getId(), EventType.REVIEW, Operation.REMOVE);
            }
            if (o instanceof Film) {
                Film film = (Film) o;
                addEvent((long) joinPoint.getArgs()[1], film.getId(), EventType.LIKE, Operation.REMOVE);
            }
            if (o instanceof User) {
                User user = (User) o;
                addEvent(user.getId(), (long) joinPoint.getArgs()[1], EventType.FRIEND, Operation.REMOVE);
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
                    review.getId(), EventType.REVIEW, Operation.UPDATE);
        }
        return joinPoint.proceed();
    }

    private void addEvent(long userId, long entityId, EventType eventType, Operation operation) {
        eventStorage.create(Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .build());
    }
}