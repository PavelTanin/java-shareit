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

    private final Set<String> emails = new HashSet<>();

    private Long id = 0L;


    @SneakyThrows
    @Override
    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            log.info("Пользователь с email:{} существует", user.getEmail());
            throw new UserNameDuplicateException("Пользователь с email:" + user.getEmail() + " уже зарегестрирован");
        }
        user.setId(getId());
        userStorage.put(user.getId(), user);
        emails.add(user.getEmail());
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
        if (emails.contains(user.getEmail())) {
            log.info("Пользователь с email:{} существует", user.getEmail());
            throw new UserNameDuplicateException("Пользователь с email:" + user.getEmail() + " уже зарегестрирован");
        }
        var updatedUser = userStorage.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            emails.remove(userStorage.get(userId).getEmail());
            emails.add(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }
        log.info("Обновлена информация о пользователе id:{}", user.getId());
        return updatedUser;
    }

    @SneakyThrows
    @Override
    public String deleteUser(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.info("Попытка удалить несуществующего пользователя c id:{}", userId);
            throw new ObjectNotFoundException("Пользователь c id:" + userId + " не зарегестрирован");
        }
        log.info("Пользователь id:{} удален", userId);
        emails.remove(userStorage.get(userId).getEmail());
        userStorage.remove(userId);
        return "Пользователь " + userId + " удален";
    }

    @SneakyThrows
    @Override
    public User findUserById(Long userId) {
        if (!userStorage.containsKey(userId)) {
            log.info("Попытка получить информацию о несуществующем пользователе c id:{}", userId);
            throw new ObjectNotFoundException("Пользователь c id:" + userId + " не зарегестрирован");
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
