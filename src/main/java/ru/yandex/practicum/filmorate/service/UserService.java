package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        if (!isFoundId(id)) {
            throw new IncorrectIdException("Некорректный id");
        }
        return userStorage.getUserById(id);
    }

    public User addFriend(Long id, Long friendId) {
        if (!isFoundId(id) || !isFoundId(friendId)) {
            throw new IncorrectIdException("Некорректный id");
        }
        userStorage.getUserById(id).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(id);
        return userStorage.getUserById(id);
    }

    public User removeFriend(Long id, Long friendId) {
        if (!isFoundId(id) || !isFoundId(friendId)) {
            throw new IncorrectIdException("Некорректный id");
        }
        userStorage.getUserById(id).removeFriend(friendId);
        userStorage.getUserById(friendId).removeFriend(id);
        return userStorage.getUserById(id);
    }

    public Collection<User> getFriends(Long id) {
        if (!isFoundId(id)) {
            throw new IncorrectIdException("Некорректный id");
        }
        return userStorage.getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        if (!isFoundId(id) || !isFoundId(otherId)) {
            throw new IncorrectIdException("Некорректный id");
        }
        Collection<Long> friends = userStorage.getUserById(otherId).getFriends();
        return userStorage.getUserById(id).getFriends().stream()
                .filter(friends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    private boolean isFoundId(Long id) {
        return userStorage.getAllUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList())
                .contains(id);
    }
}
