package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    private Film addFilm(@Valid @RequestBody Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    private Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Некорректный id фильма: {}", film);
            throw new ValidationException("Нет фильма с таким id");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);

        return film;
    }
}
