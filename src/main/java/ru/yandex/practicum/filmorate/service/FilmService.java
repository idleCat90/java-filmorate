package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(@Valid Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(@Valid Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(@NotNull Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(@NotNull Long id, @NotNull Long userId) {
        filmStorage.getFilmById(id).addLike(userId);
        return filmStorage.getFilmById(id);
    }

    public Film removeLike(@NotNull Long id, @NotNull Long userId) {
        filmStorage.getFilmById(id).removeLike(userId);
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}
