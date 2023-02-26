package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final CustomValidator customValidator;

    @SneakyThrows
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Попытка добавить нового пользователя");
        customValidator.isUserValid(userDto);
        log.info("Добавлен новый пользователь: {}", userDto.getName());
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @SneakyThrows
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("Попытка обновить информацию о пользователе id:{}", userId);
        userExist(userId);
        User user = userRepository.getReferenceById(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        log.info("Обновлена информация о пользователе id:{}", userId);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @SneakyThrows
    @Transactional
    public String deleteUser(Long userId) {
        log.info("Попытка удалить пользователя");
        userExist(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь {} удален", userId);
        return "Пользователь " + userId + " удален";
    }

    @SneakyThrows
    @Transactional
    public UserDto findUserById(Long userId) {
        log.info("Попытка получить информацию о пользователе");
        userExist(userId);
        log.info("Получена информация о пользователе {}", userId);
        return UserMapper.toUserDto(userRepository.getReferenceById(userId));
    }

    @Transactional
    public List<UserDto> findAllUsers() {
        log.info("Попытка получить информацию о всех пользователях");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public void userExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
    }

}
