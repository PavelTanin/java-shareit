package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос о создании нового пользователя {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен PATCH-запрос /users/{} с телом", userId, userDto);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        return userClient.findUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findAllUsers() {
        log.info("Получен GET-запрос /users");
        return userClient.findAllUsers();
    }

}
