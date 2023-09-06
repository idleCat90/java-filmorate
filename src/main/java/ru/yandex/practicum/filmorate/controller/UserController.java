package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserStorage userStorage;

    @GetMapping
    private List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @PostMapping
    private User createUser(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    private User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }
}
