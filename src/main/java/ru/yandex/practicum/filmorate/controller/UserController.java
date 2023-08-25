package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

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
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    private User update(@RequestBody User user) {
        users.put(user.getId(), user);
        return user;
    }
}
