package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    private User createUser(@RequestBody User user) {
        if (isValid(user)) {
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
        }
        return user;
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Некорректный id пользователя: {}", user);
            throw new ValidationException("Пользователя с таким id не существует");
        }
        if (isValid(user)) {
            if (user.getName().isBlank() || user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.info("Пустое имя пользователя, будет использован логин: {}", user);
            }
            users.put(user.getId(), user);
            log.info("Обновлён пользователь: {}", user);
        }
        return user;
    }

    private boolean isValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Некорректный адрес электронной почты {}", user);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Некорректный логин: {}", user);
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Некорректная дата рождения: {}", user);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }
}
