package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        if (!isFoundId(id)) {
            throw new IncorrectIdException("Фильм не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public Film addLike(Long id, Long userId) {
        if (!isFoundId(id)) {
            throw new IncorrectIdException("Фильм не найден");
        }
        filmStorage.getFilmById(id).addLike(userId);
        return filmStorage.getFilmById(id);
    }

    public Film removeLike(Long id, Long userId) {
        if (!isFoundId(id)) {
            throw new IncorrectIdException("Фильм не найден");
        }
        if (!filmStorage.getFilmById(id).getLikes().contains(userId)) {
            throw new IncorrectIdException("Лайк от этого пользователя не найден");
        }
        filmStorage.getFilmById(id).removeLike(userId);
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isFoundId(Long id) {
        return filmStorage.getAllFilms().stream()
                .map(Film::getId)
                .collect(Collectors.toList())
                .contains(id);
    }

}
