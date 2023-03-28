package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity createUser(@RequestBody UserDto userDto) {
        log.info("Получен PUT-запрос /users c телом {}", userDto);
        return new ResponseEntity(userService.createUser(userDto), HttpStatus.OK);
    }

    @PatchMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateUser(@RequestBody UserDto userDto, @PathVariable(value = "userId") Long userId) {
        log.info("Получен PATCH-запрос /users/{} с телом", userId, userDto);
        return new ResponseEntity(userService.updateUser(userDto, userId), HttpStatus.OK);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info("Получен DELETE-запрос /users/{}", userId);
        return new ResponseEntity(new String[]{userService.deleteUser(userId)}, HttpStatus.OK);
    }

    @GetMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findUserById(@PathVariable(value = "userId") Long userId) {
        log.info("Получен GET-запрос /users/{}", userId);
        return new ResponseEntity(userService.findUserById(userId), HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findAllUsers() {
        log.info("Получен GET-запрос /users");
        return new ResponseEntity(userService.findAllUsers(), HttpStatus.OK);
    }
}
