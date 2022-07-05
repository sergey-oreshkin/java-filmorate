package ru.yandex.practicum.filmorate.storage.eventstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rs-popov
 * Класс для связи с базой данных по функции "Лента событий"
 */

@Primary
@Component
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getEventsByUserId(long id) {
        String sql = "select * from events where user_id=?";
        return jdbcTemplate.query(sql, this::mapRowToEvent, id);
    }

    public Event create(Event event) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("events").usingGeneratedKeyColumns("event_id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", event.getUserId())
                .addValue("entity_id", event.getEntityId())
                .addValue("eventType", event.getEventType())
                .addValue("operation", event.getOperationType())
                .addValue("event_time", Timestamp.valueOf(LocalDateTime.now()));

        Number num = jdbcInsert.executeAndReturnKey(parameters);
        event.setEventId(num.longValue());
        return event;
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) {
            throw new NotFoundException("Event not found.");
        }
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .operationType(OperationType.valueOf(rs.getString("operation")))
                .timestamp(rs.getTimestamp("event_time").getTime())
                .build();
    }
}