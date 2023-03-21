package ru.practicum.shareit.exception;

public class UserEmailValidationException extends RuntimeException {

    public UserEmailValidationException(String message) {
        super(message);
    }
}
