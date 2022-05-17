package ru.yandex.practicum.filmorate.model;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
public class Film {

    @NonFinal
    @Setter
    long id;

    @NotBlank
    String name;

    @NotBlank
    @Size(max = 200)
    String description;

    LocalDate releaseDate;

    @Positive
    int duration;

    private final Set<Long> likes = new HashSet<>();

    public void setLike(long id) {
        likes.add(id);
    }

    public void deleteLike(long id) {
        likes.remove(id);
    }

    public int rate() {
        return likes.size();
    }
}
