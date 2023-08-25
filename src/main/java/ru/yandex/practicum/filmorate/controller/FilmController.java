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
@RestController("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    private List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    private Film addFilm(@RequestBody Film film) {
//        if (films.containsKey(film.getId())) {
//            log.error("Фильм уже есть в списке: {}", film.toString());
//            throw new ValidationException("Такой фильм уже есть в списке.");
//        }
        if (isValid(film)) {
            log.info("Добавлен фильм: {}", film);
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping
    private Film updateFilm(@RequestBody Film film) {
        if (isValid(film)) {
            log.info("Обновлён фильм: {}", film);
            films.put(film.getId(), film);
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
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.error("Некорректная продролжительность фильма: {}", film);
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        return true;
    }
}
