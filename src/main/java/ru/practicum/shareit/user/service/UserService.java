package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto findUserById(Long userId);

    String deleteUser(Long userId);

    List<UserDto> findAllUsers();
}
