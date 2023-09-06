package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    private Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    private User createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    private User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
