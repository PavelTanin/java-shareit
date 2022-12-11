package ru.practicum.shareit.user.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNameDuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserMemoryRepository implements UserRepository {

    private final Map<Long, User> userStorage = new HashMap<>();

    private Long id = 0L;


    @SneakyThrows
    @Override
    public User createUser(User user) {
        if (userStorage.values().stream().anyMatch(o -> o.getEmail().equals(user.getEmail()))) {
            log.info("Пользователь с таким email существует");
            throw new UserNameDuplicateException("Пользователь с таким email уже зарегестрирован");
        }
        user.setId(getId());
        userStorage.put(user.getId(), user);
        log.info("Добавлен новый пользователь, id:{}", user.getId());
        return user;
    }

    @SneakyThrows
    @Override
    public User updateUser(User user, Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.info("Попытка обновить несуществующего пользователя");
            throw new ObjectNotFoundException("Такой пользователь не зарегестрирован");
        }
        if (userStorage.values().stream().anyMatch(o -> o.getEmail().equals(user.getEmail()))) {
            log.info("Пользователь с таким email существует");
            throw new UserNameDuplicateException("Пользователь с таким email уже зарегестрирован");
        }
        var updatedUser = userStorage.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        log.info("Обновлена информация о пользователе id:{}", user.getId());
        return updatedUser;
    }

    @SneakyThrows
    @Override
    public String deleteUser(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.info("Попытка удалить несуществующего пользователя");
            throw new ObjectNotFoundException("Такой пользователь не зарегестрирован");
        }
        log.info("Пользователь id:{} удален", userId);
        userStorage.remove(userId);
        return "Пользователь " + userId + " удален";
    }

    @SneakyThrows
    @Override
    public User findUserById(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.info("Пользователь с id: не зарегестрирован", userId);
            throw new ObjectNotFoundException("Такой пользователь не зарегестрирован");
        }
        return userStorage.get(userId);
    }

    @Override
    public List<User> findAllUsers() {
        log.info("Получен список всех пользователей");
        return new ArrayList<>(userStorage.values());
    }

    private Long getId() {
        id++;
        return id;
    }

    @Override
    public boolean contains(Long userId) {
        return userStorage.containsKey(userId);
    }
}
