package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

@Data
public class Film {

    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Long> likes;

    public Collection<Long> getLikes() {
        return new ArrayList<>(likes);
    }

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        likes.remove(id);
    }
}
