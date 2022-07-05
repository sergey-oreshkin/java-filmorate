package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.NonFinal;

import javax.validation.constraints.Size;

/**
 * @author rs-popov
 * Базовый класс с полями по функции "Лента событий"
 */

@Value
@Builder
public class Event {

    @Setter
    @NonFinal
    long eventId;

    @NonNull
    long userId;

    @NonNull
    long entityId;

    @NonNull
    @Size(max = 10)
    EventType eventType;

    @NonNull
    @Size(max = 10)
    OperationType operationType;

    @NonNull
    long timestamp;
}