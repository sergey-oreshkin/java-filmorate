package ru.yandex.practicum.filmorate.storage.eventstorage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    List<Event> getEventsByUserId(long id);

    Event create(Event event);

}