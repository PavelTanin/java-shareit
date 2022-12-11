package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final CustomValidator customValidator;

    @SneakyThrows
    @Override
    public UserDto createUser(User user) {
        log.info("Попытка добавить нового пользователя");
        customValidator.isUserValid(user);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @SneakyThrows
    @Override
    public UserDto updateUser(User user, Long userId) {
        log.info("Попытка обновить информацию о пользователе");
        return UserMapper.toUserDto(userRepository.updateUser(user, userId));
    }

    @SneakyThrows
    @Override
    public String deleteUser(Long userId) {
        log.info("Попытка удалить пользователя");
        return userRepository.deleteUser(userId);
    }

    @SneakyThrows
    @Override
    public UserDto findUserById(Long userId) {
        log.info("Попытка получить информацию о пользователе");
        return UserMapper.toUserDto(userRepository.findUserById(userId));
    }

    @Override
    public List<UserDto> findAllUsers() {
        log.info("Попытка получить информацию о всех пользователях");
        return userRepository.findAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
