package ru.practicum.shareit.exception;

public class UserNameDuplicateException extends RuntimeException {

    public UserNameDuplicateException(String message) {
        super(message);
    }
}
