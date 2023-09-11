package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
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
        var film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("filmService: фильм с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id фильма");
        }
        return film;
    }

    public Film addLike(Long id, Long userId) {
        var film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("filmService: фильм с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id фильма");
        }
        // TODO проверить валидность пользователя
        film.addLike(userId);
        return film;
    }

    public Film removeLike(Long id, Long userId) {
        var film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("filmService: фильм с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id фильма");
        }
        if (!film.getLikes().contains(userId)) {
            log.error("filmService: лайк от пользователя с таким id не найден: {}", userId);
            throw new IncorrectIdException("Неверный id пользователя");
        }
        film.removeLike(userId);
        return film;
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
