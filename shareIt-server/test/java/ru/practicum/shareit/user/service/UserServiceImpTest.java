package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private CustomValidator customValidator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImp userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void createUserWhenUserIsValidThenSaveUser() {
        UserDto testUser = new UserDto("test@test.ru", "Test");
        User user = UserMapper.toUser(testUser);
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.createUser(testUser);

        verify(customValidator, times(1)).isUserValid(testUser);
        assertEquals(testUser, result);

    }

    @Test
    void createUserWhenUserNotValidNameWhenThrowUserEmptyNameException() {
        UserDto testUser = new UserDto("test@test.ru", "");
        doThrow(UserEmptyNameException.class).when(customValidator).isUserValid(testUser);
        User user = UserMapper.toUser(testUser);

        assertThrows(UserEmptyNameException.class, () -> userService.createUser(testUser));

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUserWhenUserIsExistThenSaveUser() {
        Long userId = 1L;
        UserDto testUser = new UserDto("test@test.ru", "Test");
        UserDto newUser = new UserDto(1L, "test@test.ru", "Renamed");
        User user = UserMapper.toUser(newUser);
        user.setId(userId);
        newUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateUser(newUser, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        UserDto captured = UserMapper.toUserDto(userArgumentCaptor.getValue());

        assertEquals(captured, result);


    }

    @Test
    void updateUserWhenUserIsNotExistThenSaveUser() {
        Long userId = 0L;
        UserDto testUser = new UserDto("test@test.ru", "Test");
        User user = UserMapper.toUser(testUser);

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(testUser, userId));

        verify(userRepository, never()).save(user);

    }

    @Test
    void deleteUserWhenUserExistThenDeleteUser() {
        Long userId = 0L;
        when(userRepository.existsById(userId)).thenReturn(true);

        String result = userService.deleteUser(userId);

        assertEquals("Пользователь " + userId + " удален", result);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserWhenUserNotExistThenThrow() {
        Long userId = 0L;

        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, never()).deleteById(userId);

    }

    @Test
    void findUserByIdWhenUserExistThenGetUser() {
        Long userId = 1L;
        UserDto testUser = new UserDto(1L, "test@test.ru", "Test");
        User user = UserMapper.toUser(testUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.findUserById(userId);

        assertEquals(testUser, result);

    }

    @Test
    void findUserByIdWhenUserNotExistThenThrowObjectNotFoundException() {
        Long userId = 0L;

        assertThrows(ObjectNotFoundException.class, () -> userService.findUserById(userId));

        verify(userRepository, never()).getReferenceById(userId);

    }

    @Test
    void findAllUsersWhenUsersListNotEmptyThenGetUsersList() {
        UserDto testUser = new UserDto(0L, "test@test.ru", "Test");
        UserDto testUser2 = new UserDto(1L, "test2@test.ru", "Test2");
        UserDto testUser3 = new UserDto(2L, "test3@test.ru", "Test3");
        List<User> userList = List.of(testUser, testUser2, testUser3).stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());
        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> result = userService.findAllUsers();

        assertAll(
                () -> assertEquals(testUser, result.get(0)),
                () -> assertEquals(testUser2, result.get(1)),
                () -> assertEquals(testUser3, result.get(2))
        );
    }

    @Test
    void findAllUsersWhenUsersListIsEmptyThenGetEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.findAllUsers();

        assertEquals(Collections.emptyList(), result);
    }
}