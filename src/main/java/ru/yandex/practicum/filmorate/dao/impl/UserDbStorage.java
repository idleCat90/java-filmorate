package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM public.users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO public.users\n" +
                "(email, login, name, birthday)\n" +
                "VALUES(?, ?, ?, ?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        int key = (int) keyHolder.getKey();
        user.setId(key);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE public.users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?;";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM public.users WHERE id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        if (!set.next()) {
            return Optional.empty();
        } else {
            return Optional.of(User.builder()
                    .id(set.getInt("id"))
                    .email(set.getString("email"))
                    .login(set.getString("login"))
                    .name(set.getString("name"))
                    .birthday(Objects.requireNonNull(set.getDate("birthday")).toLocalDate())
                    .build());
        }
    }

    @Override
    public List<User> getFriends(int id) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM public.friends f " +
                "JOIN public.users u ON f.friend_id = u.id WHERE f.user_id = ?\n" +
                "UNION\n" +
                "SELECT u.id, u.email, u.login, u.name, u.birthday FROM public.friends f " +
                "JOIN public.users u ON f.user_id = u.id WHERE (f.friend_id = ? AND f.accepted = true);";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, id);
    }

    @Override
    public Boolean isUserPresent(int id) {
        String sql = "SELECT id FROM public.users WHERE id = ?;";
        SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
        return set.next();
    }

    @Override
    public void checkAndUpdateFriends(int id, int friendId) {
        String sql = "SELECT * FROM public.friends WHERE user_id = ? AND friend_id = ?;";
        SqlRowSet setDirect = jdbcTemplate.queryForRowSet(sql, id, friendId);
        SqlRowSet setReversed = jdbcTemplate.queryForRowSet(sql, friendId, id);
        boolean isDirectNotEmpty = setDirect.next();
        boolean isReversedNotEmpty = setReversed.next();
        if (!isDirectNotEmpty && !isReversedNotEmpty) {
            String sqlInsert = "INSERT INTO public.friends (user_id, friend_id, accepted) VALUES (?, ?, ?);";
            jdbcTemplate.update(sqlInsert, id, friendId, false);
        } else if (isReversedNotEmpty) {
            String sqlUpdate = "UPDATE public.friends SET friend_id = ?, accepted = true WHERE user_id = ?;";
            jdbcTemplate.update(sqlUpdate, id, friendId);
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sql = "SELECT * FROM public.friends WHERE user_id = ? AND friend_id = ?;";
        SqlRowSet setDirect = jdbcTemplate.queryForRowSet(sql, id, friendId);
        boolean isDirectNotEmpty = setDirect.next();
        if (isDirectNotEmpty) {
            boolean ifApproved = setDirect.getBoolean("accepted");
            String sqlDelete = "DELETE FROM public.friends WHERE user_id = ? AND friend_id = ?;";
            if (!ifApproved) {
                jdbcTemplate.update(sqlDelete, id, friendId);
            } else {
                jdbcTemplate.update(sqlDelete, id, friendId);
                String sqlUpdate = "INSERT INTO public.friends (user_id, friend_id, accepted) VALUES (?, ?, ?);";
                jdbcTemplate.update(sqlUpdate, friendId, id, false);
            }
        } else {
            SqlRowSet setReversed = jdbcTemplate.queryForRowSet(sql, friendId, id);
            boolean isReversedNotEmpty = setReversed.next();
            if (isReversedNotEmpty) {
                boolean ifApproved = setReversed.getBoolean("accepted");
                if (ifApproved) {
                    String sqlUpdate = "INSERT INTO public.friends (user_id, friend_id, accepted) VALUES (?, ?, ?);";
                    jdbcTemplate.update(sqlUpdate, friendId, id, false);
                }
            }
        }
    }

    @Override
    public List<User> getCommonFriends(int id1, int id2) {
        String sql = "SELECT * FROM users u\n" +
                "WHERE id IN\n" +
                "(\n" +
                "SELECT *\n" +
                "FROM\n" +
                "(SELECT f.friend_id AS friends\n" +
                "FROM friends f\n" +
                "WHERE f.user_id = ?\n" +
                "UNION\n" +
                "SELECT f.user_id\n" +
                "FROM friends f \n" +
                "WHERE (f.friend_id = ? AND f.accepted = true))\n" +
                "WHERE friends IN (\n" +
                "SELECT f.friend_id AS friends\n" +
                "FROM friends f\n" +
                "WHERE f.user_id = ?\n" +
                "UNION\n" +
                "SELECT f.user_id\n" +
                "FROM friends f \n" +
                "WHERE (f.friend_id = ? AND f.accepted = true))\n" +
                ");";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id1, id1, id2, id2);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}