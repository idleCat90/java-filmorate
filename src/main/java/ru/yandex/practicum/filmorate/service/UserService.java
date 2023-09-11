package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
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
        var user = userStorage.getUserById(id);
        if (user == null) {
            log.error("userService: пользователь с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        return user;
    }

    public User addFriend(Long id, Long friendId) {
        var user = userStorage.getUserById(id);
        var friend = userStorage.getUserById(friendId);
        if (user == null) {
            log.error("userService: пользователь с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        if (friend == null) {
            log.error("userService: пользователь с таким id не найден: {}", friendId);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        user.addFriend(friendId);
        friend.addFriend(id);
        log.info("userService: у пользователя новый друг: {}", user);
        return user;
    }

    public User removeFriend(Long id, Long friendId) {
        var user = userStorage.getUserById(id);
        var friend = userStorage.getUserById(friendId);
        if (user == null) {
            log.error("userService: пользователь с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        if (friend == null) {
            log.error("userService: пользователь с таким id не найден: {}", friendId);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        if (!user.getFriends().contains(friend.getId())) {
            log.error("В друзьях нет пользователя с таким id: {}", friendId);
            throw new IncorrectIdException("Неверный id пользователя");
        }
        user.removeFriend(friendId);
        friend.removeFriend(id);
        log.info("userService: у пользователя удалён друг: {}", user);
        return userStorage.getUserById(id);
    }

    public Collection<User> getFriends(Long id) {
        var user = userStorage.getUserById(id);
        if (user == null) {
            log.error("userService: пользователь с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        var user = userStorage.getUserById(id);
        var other = userStorage.getUserById(otherId);
        if (user == null) {
            log.error("userService: пользователь с таким id не найден: {}", id);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        if (other == null) {
            log.error("userService: пользователь с таким id не найден: {}", otherId);
            throw new IncorrectIdException("Несуществующий id пользователя");
        }
        Collection<Long> otherFriends = other.getFriends();
        return user.getFriends().stream()
                .filter(otherFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }
}
