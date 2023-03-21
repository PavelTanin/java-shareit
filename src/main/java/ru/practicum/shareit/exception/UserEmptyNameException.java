package ru.practicum.shareit.exception;

public class UserEmptyNameException extends RuntimeException {

    public UserEmptyNameException(String message) {
        super(message);
    }
}
