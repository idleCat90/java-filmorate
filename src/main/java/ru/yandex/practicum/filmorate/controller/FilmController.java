package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

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
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    private Film update(@RequestBody Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
