package ru.yandex.practicum.filmorate.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT rating FROM public.ratings WHERE id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        if (set.next()) {
            String mpaName = set.getString("rating");
            Mpa mpa = new Mpa(id, mpaName);
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }

    public List<Mpa> getMpaList() {
        String sql = "SELECT * FROM public.ratings;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("rating");
        return new Mpa(id, name);
    }
}
