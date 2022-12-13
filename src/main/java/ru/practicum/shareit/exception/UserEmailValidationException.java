package ru.practicum.shareit.exception;

import java.io.IOException;

public class UserEmailValidationException extends IOException {

    public UserEmailValidationException(String message) {
        super(message);
    }
}
