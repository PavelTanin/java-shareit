package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserMemoryRepository implements UserRepository {

    private final Map<Long, User> userStorage = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    private Long id = 0L;

    @Override
    public User createUser(User user) {
        user.setId(getId());
        userStorage.put(user.getId(), user);
        emails.add(user.getEmail());
        log.info("Добавлен новый пользователь, id:{}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user, Long userId) {
        var updatedUser = userStorage.get(userId);
        updatedUser.setName(user.getName());
        emails.remove(userStorage.get(userId).getEmail());
        emails.add(user.getEmail());
        updatedUser.setEmail(user.getEmail());
        log.info("Обновлена информация о пользователе id:{}", user.getId());
        return updatedUser;
    }

    @Override
    public User updateUserWithoutEmail(User user, Long userId) {
        var updatedUser = userStorage.get(userId);
        updatedUser.setName(user.getName());
        log.info("Обновлено имя пользователя id:{}", user.getId());
        return updatedUser;
    }

    @Override
    public User updateUserWithoutName(User user, Long userId) {
        var updatedUser = userStorage.get(userId);
        emails.remove(userStorage.get(userId).getEmail());
        emails.add(user.getEmail());
        updatedUser.setEmail(user.getEmail());
        log.info("Обновлен email пользователя id:{}", user.getId());
        return updatedUser;
    }

    @Override
    public String deleteUser(Long userId) {
        log.info("Пользователь id:{} удален", userId);
        emails.remove(userStorage.get(userId).getEmail());
        userStorage.remove(userId);
        return "Пользователь " + userId + " удален";
    }

    @Override
    public User findUserById(Long userId) {
        log.info("Получена информаци о пользователе id:{}", userId);
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

    @Override
    public boolean emailContains(String email) {
        return emails.contains(email);
    }
}
