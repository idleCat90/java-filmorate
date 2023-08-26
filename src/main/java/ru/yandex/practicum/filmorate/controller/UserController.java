package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    private List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    private User createUser(@Valid @RequestBody User user) {
        user.setId(++id);
        try {
            if (user.getName().isBlank() || user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.info("Пустое имя пользователя, будет использован логин: {}", user);
            }
        } catch (NullPointerException e) {
            user.setName(user.getLogin());
            log.info("Имя пользователя null, будет использован логин: {}", user);
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    private User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Некорректный id пользователя: {}", user);
            throw new ValidationException("Пользователя с таким id не существует");
        }
        if (user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пустое имя пользователя, будет использован логин: {}", user);
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }
}
