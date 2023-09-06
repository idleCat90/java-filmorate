package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
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
        userStorage.getUserById(id).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(id);
        return userStorage.getUserById(id);
    }

    public User removeFriend(Long id, Long friendId) {
        userStorage.getUserById(id).removeFriend(friendId);
        userStorage.getUserById(friendId).removeFriend(id);
        return userStorage.getUserById(id);
    }

    public Collection<User> findCommonFriends(Long id, Long otherId) {
        Collection<Long> friends = userStorage.getUserById(otherId).getFriends();
        return userStorage.getUserById(id).getFriends().stream()
                .filter(friends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }
}
