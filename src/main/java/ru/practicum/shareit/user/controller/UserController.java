package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен PUT-запрос /users c телом {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable(value = "userId") Long userId) {
        log.info("Получен PATCH-запрос /users/{} с телом", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        return userService.deleteUser(userId);
    }

    @GetMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findUserById(@PathVariable(value = "userId") Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        return userService.findUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAllUsers() {
        log.info("Получен GET-запрос /users");
        return userService.findAllUsers();
    }
}
