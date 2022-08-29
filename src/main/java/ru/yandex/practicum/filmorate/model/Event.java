package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
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
    Operation operation;

    @NonNull
    long timestamp;
}