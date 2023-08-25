package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    private List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    private Film addFilm(@RequestBody Film film) {
        if (isValid(film)) {
            film.setId(++id);
            films.put(film.getId(), film);
            log.info("Добавлен фильм: {}", film);
        }
        return film;
    }

    @PutMapping
    private Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Некорректный id фильма: {}", film);
            throw new ValidationException("Нет фильма с таким id");
        }
        if (isValid(film)) {
            films.put(film.getId(), film);
            log.info("Обновлён фильм: {}", film);
        }
        return film;
    }

    private boolean isValid(Film film) {
        if (film.getName().isBlank()) {
            log.error("Некорректное название: {}", film);
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Некорректная длина описания: {}", film);
            throw new ValidationException("Максимальная длина описания - 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Некорректная дата релиза: {}", film);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.error("Некорректная продролжительность фильма: {}", film);
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        return true;
    }
}
