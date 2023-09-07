package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long idCount = 0L;

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пустое имя пользователя, будет использован логин: {}", user);
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Некорректный id пользователя: {}", user);
            throw new IncorrectIdException("Пользователя с id=" + user.getId() + " не существует");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пустое имя пользователя, будет использован логин: {}", user);
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("Не найден пользователь с id={}", userId);
            throw new IncorrectIdException("Не найден пользователь с id=" + userId);
        }
        return users.get(userId);
    }

    private Long generateId() {
        return ++idCount;
    }
}
