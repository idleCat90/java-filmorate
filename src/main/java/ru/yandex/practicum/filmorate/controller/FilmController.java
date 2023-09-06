package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmStorage filmStorage;

    @GetMapping
    private List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PostMapping
    private Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    private Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }


}
