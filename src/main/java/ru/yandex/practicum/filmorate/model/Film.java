package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.constraint.DateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @Null
    @EqualsAndHashCode.Exclude
    private Integer id;

    @NotBlank(message = "Title of film must be not empty")
    private String name;

    @NotNull(message = "Description of film must be not null")
    @Size(min = 10, max = 200, message = "Description length is max=200 and min=10")
    private String description;

    @DateRelease(day = 28, month = 12, year = 1895, message = "Date of release must be after than 28 December 1895")
    private LocalDate releaseDate;

    @Positive(message = "Duration of film must be positive value")
    private int duration;
}
