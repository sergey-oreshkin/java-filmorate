package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"name"})
public class Genre {

    private final int id;

    @Setter
    private String name;
}
