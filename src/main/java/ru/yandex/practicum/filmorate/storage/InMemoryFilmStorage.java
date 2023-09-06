package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long idCount = 0L;

    @Override
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Некорректный id фильма: {}", film);
            throw new ValidationException("Нет фильма с таким id");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    private Long generateId() {
        return ++idCount;
    }
}
