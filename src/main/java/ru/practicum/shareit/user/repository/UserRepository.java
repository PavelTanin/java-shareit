package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user, Long userId);

    String deleteUser(Long userId);

    User findUserById(Long userId);

    List<User> findAllUsers();

    boolean contains(Long userId);
}
