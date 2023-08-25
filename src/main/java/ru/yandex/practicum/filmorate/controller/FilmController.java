package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    private List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    private Film add(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Такой фильм уже есть в списке.");
        }
        if (isValid(film)) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping
    private Film update(@RequestBody Film film) {
        if (isValid(film)) {
            films.put(film.getId(), film);
        }
        return film;
    }

    private boolean isValid(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        return true;
    }
}
