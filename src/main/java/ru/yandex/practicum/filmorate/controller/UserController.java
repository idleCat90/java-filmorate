package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    private Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    private User createUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }

    @PutMapping("/users")
    private User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/users/{id}")
    private User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    private User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    private User removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    private Collection<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    private Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
