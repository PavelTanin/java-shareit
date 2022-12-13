package ru.practicum.shareit.exception;

import java.io.IOException;

public class UserNameDuplicateException extends IOException {

    public UserNameDuplicateException(String message) {
        super(message);
    }
}
