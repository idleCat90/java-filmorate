package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String title;
    @Size(max = 200)
    @NotNull
    private String description;
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @NotNull
    private Mpa mpa;
    private List<Genre> genres;
    private int likesCount;
}
