package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.yandex.practicum.filmorate.constraint.DateRelease;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private Long id;

    @NotBlank(message = "Title of film must be not empty")
    private String name;

    @Size(min = 10, max = 200, message = "Length of film description: max = 200 and min = 10")
    @NotNull(message = "Description of film must be not null")
    private String description;

    @DateRelease(day = 28, month = 12, year = 1895, message = "Date of release must be after 28 December 1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration of film must be positive value")
    private Long duration;

    @JsonIgnore
    private Set<Long> userLikes = new HashSet<>();

    private Integer rate;

    private Mpa mpa;

    private Set<Genre> genres;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(name, film.name)
                && Objects.equals(description, film.description)
                && Objects.equals(releaseDate, film.releaseDate)
                && Objects.equals(duration, film.duration);
    }
}
