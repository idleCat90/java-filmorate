package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    private List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    private User create(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " уже существует");
        }
        if (isValid(user)) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @PutMapping
    private User update(@RequestBody User user) {
        if (isValid(user)) {
            users.put(user.getId(), user);
        }
        return user;
    }

    private boolean isValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логине не может быть пустым и содержать пробелы");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }
}
