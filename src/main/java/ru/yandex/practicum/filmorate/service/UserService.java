package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(@Valid User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(@Valid User user) {
        return userStorage.updateUser(user);
    }

    public User addFriend(Long id, Long friendId) {
        userStorage.getUserById(id).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(id);
        return userStorage.getUserById(id);
    }

    public User removeFriend(Long id, Long friendId) {
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
        return userStorage.getUserById(id);
    }

    public Collection<User> findCommonFriends(Long id, Long otherId) {
        Set<Long> friends2 = userStorage.getUserById(otherId).getFriends();
        return userStorage.getUserById(id).getFriends().stream()
                .filter(friends2::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }
}
