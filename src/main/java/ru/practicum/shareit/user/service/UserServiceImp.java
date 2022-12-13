package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNameDuplicateException;
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
    public UserDto createUser(UserDto userDto) {
        log.info("Попытка добавить нового пользователя");
        if (userRepository.emailContains(userDto.getEmail())) {
            log.info("Пользователь с email:{} существует", userDto.getEmail());
            throw new UserNameDuplicateException("Пользователь с email:" + userDto.getEmail() + " уже зарегестрирован");
        }
        final User user = UserMapper.toUser(userDto);
        customValidator.isUserValid(user);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @SneakyThrows
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("Попытка обновить информацию о пользователе id:{}", userId);
        if (!userRepository.contains(userId)) {
            log.info("Попытка обновить несуществующего пользователя");
            throw new ObjectNotFoundException("Такой пользователь не зарегестрирован");
        }
        if (userRepository.emailContains(userDto.getEmail())) {
            log.info("Пользователь с email:{} существует", userDto.getEmail());
            throw new UserNameDuplicateException("Пользователь с email:" + userDto.getEmail() + " уже зарегестрирован");
        }
        if (userDto.getName() == null) {
            return UserMapper.toUserDto(userRepository.updateUserWithoutName(UserMapper.toUser(userDto), userId));
        }
        if (userDto.getEmail() == null) {
            return UserMapper.toUserDto(userRepository.updateUserWithoutEmail(UserMapper.toUser(userDto), userId));
        }
        return UserMapper.toUserDto(userRepository.updateUser(UserMapper.toUser(userDto), userId));
    }

    @SneakyThrows
    @Override
    public String deleteUser(Long userId) {
        log.info("Попытка удалить пользователя");
        if (!userRepository.contains(userId)) {
            log.info("Попытка удалить несуществующего пользователя c id:{}", userId);
            throw new ObjectNotFoundException("Пользователь c id:" + userId + " не зарегестрирован");
        }
        return userRepository.deleteUser(userId);
    }

    @SneakyThrows
    @Override
    public UserDto findUserById(Long userId) {
        log.info("Попытка получить информацию о пользователе");
        if (!userRepository.contains(userId)) {
            log.info("Попытка получить информацию о несуществующем пользователе c id:{}", userId);
            throw new ObjectNotFoundException("Пользователь c id:" + userId + " не зарегестрирован");
        }
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
