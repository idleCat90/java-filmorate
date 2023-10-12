package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getGenreList() {
        String sql = "SELECT * FROM public.genres;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT genre FROM public.genres WHERE id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        if (set.next()) {
            Genre genre = new Genre(
                    id,
                    set.getString("genre")
            );
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("genre")
        );
    }
}
