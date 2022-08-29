/**
 * Класс данных режиссер
 *
 * @author Vladimir Arkhipenko
 */
package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"name"})
public class Director {

    private long id;
    @NotBlank
    @Size(max = 50)
    private String name;
}
