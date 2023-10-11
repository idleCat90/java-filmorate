package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM public.films f JOIN ratings r ON f.rating_id = r.id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        Map<Integer, List<Genre>> genres = getGenres(films);
        for (Film film : films) {
            int id = film.getId();
            List<Genre> genreList = genres.get(id);
            film.setGenres(Objects.requireNonNullElseGet(genreList, ArrayList::new));
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getId());
        int mpaId = film.getMpa().getId();
        String sql = "INSERT INTO public.films\n" +
                "(id, title, description, release_date, duration, rating_id)\n" +
                "VALUES(?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), mpaId);
        film.setMpa(makeMpa(mpaId));
        film.setLikesCount(getLikesSet(film.getId()).size());
        if (film.getGenres() != null) {
            updateFilmGenre(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int mpaId = film.getMpa().getId();
        String sql = "UPDATE public.films SET " +
                "title = ?, description = ?, release_date = ?, duration = ?, rating_id = ?\n" +
                "WHERE id = ?;";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), mpaId, film.getId());
        film.setMpa(makeMpa(mpaId));
        film.setLikesCount(getLikesSet(film.getId()).size());
        if (film.getGenres() != null) {
            updateFilmGenre(film);
        }
        Map<Integer, List<Genre>> genres = getGenres(List.of(film));
        int filmId = film.getId();
        List<Genre> genreList = genres.get(filmId);
        film.setGenres(Objects.requireNonNullElseGet(genreList, ArrayList::new));
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM public.films f JOIN ratings r ON f.rating_id = r.id WHERE f.id = ?";
        List<Film> query = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (!query.isEmpty()) {
            Map<Integer, List<Genre>> genres = getGenres(query);
            for (Film film : query) {
                int filmId = film.getId();
                List<Genre> genreList = genres.get(filmId);
                film.setGenres(Objects.requireNonNullElseGet(genreList, ArrayList::new));
            }
            return query.get(0);
        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }

    }

    @Override
    public boolean isFilmPresent(int id) {
        String sql = "SELECT id FROM public.films WHERE id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        return set.next();
    }

    @Override
    public void addLikeToFilm(int id, int userId) {
        String sql = "SELECT * FROM public.likes WHERE film_id = ? AND user_id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id, userId);
        if (!set.next()) {
            sql = "INSERT INTO public.likes (film_id, user_id) VALUES(?, ?)";
            jdbcTemplate.update(sql, id, userId);
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        String sql = "SELECT * FROM public.likes WHERE film_id = ? AND user_id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id, userId);
        if (set.next()) {
            sql = "DELETE FROM public.likes WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(sql, id, userId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT * FROM public.films f\n" +
                "JOIN public.ratings r ON f.rating_id = r.id\n" +
                "WHERE f.id IN (SELECT film_id FROM public.likes GROUP BY film_id ORDER BY COUNT(user_id) DESC);";
        List<Film> query = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        if (!query.isEmpty()) {
            Map<Integer, List<Genre>> genres = getGenres(query);
            for (Film film : query) {
                int filmId = film.getId();
                List<Genre> genreList = genres.get(filmId);
                film.setGenres(Objects.requireNonNullElseGet(genreList, ArrayList::new));
            }
            return query;
        } else {
            return getFilms();
        }
    }

    private void updateFilmGenre(Film film) {
        String sqlDelete = "DELETE FROM public.film_genre WHERE film_id = ?;";
        jdbcTemplate.update(sqlDelete, film.getId());
        Set<Genre> genreSet = new HashSet<>(film.getGenres());
        System.out.println(genreSet);
        ArrayList<Genre> genres = new ArrayList<>(genreSet);
        System.out.println(genres);
        jdbcTemplate.batchUpdate(
                "INSERT INTO public.film_genre (film_id, genre_id) VALUES(?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private int getId() {
        String sqlGetId = "SELECT COUNT(id) AS id FROM public.films;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sqlGetId);
        set.next();
        return set.getInt(1) + 1;
    }

    private Film makeFilm(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            Film film = Film.builder()
                    .id(id)
                    .name(rs.getString("title"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(new Mpa(
                            rs.getInt("rating_id"),
                            rs.getString("rating")
                    ))
                    .likesCount(getLikesSet(id).size())
                    .build();
            film.setLikesCount(getLikesSet(film.getId()).size());
            return film;
        } catch (SQLException e) {
            throw new NotFoundException("Не удалось найти фильм.");
        }
    }

    private Map<Integer, List<Genre>> makeGenre(ResultSet rs) throws SQLException {
        Map<Integer, List<Genre>> map = new HashMap<>();
        Genre genre = new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre"));
        List<Genre> genres = new ArrayList<>(List.of(genre));
        map.put(rs.getInt("film_id"), genres);
        return map;
    }

    private Mpa makeMpa(int id) {
        String sql = "SELECT rating FROM public.ratings WHERE id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        set.next();
        String mpaName = set.getString("rating");
        return new Mpa(id, mpaName);
    }

    private Map<Integer, List<Genre>> getGenres(List<Film> films) {
        List<Integer> filmsId = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
        String sql = "SELECT * " +
                "FROM public.film_genre fg JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (%s)";
        List<Map<Integer, List<Genre>>> maps = jdbcTemplate.query(
                String.format(sql, inSql),
                filmsId.toArray(),
                (rs, rowNum) -> makeGenre(rs)
        );
        Map<Integer, List<Genre>> map = new HashMap<>();
        for (Map<Integer, List<Genre>> currentMap : maps) {
            for (Integer id : currentMap.keySet()) {
                List<Genre> list = map.get(id);
                if (list != null) {
                    list.add(currentMap.get(id).get(0));
                } else {
                    list = currentMap.get(id);
                }
                map.put(id, list);
            }
        }
        return map;
    }

    private int getLikesById(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

    private Set<Integer> getLikesSet(int id) {
        String sql = "SELECT user_id FROM public.likes WHERE film_id = ?;";
        List<Integer> query;
        query = jdbcTemplate.query(sql, (rs, rowNum) -> getLikesById(rs), id);
        return new LinkedHashSet<>(query);
    }
}