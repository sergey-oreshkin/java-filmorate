package ru.yandex.practicum.filmorate.feedAOP;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rs-popov
 * Аннотация для методов создания лайков, ревью и добавления друга
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CreatingEvent {
}