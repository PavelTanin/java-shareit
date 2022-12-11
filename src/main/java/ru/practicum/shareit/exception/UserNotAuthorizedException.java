package ru.practicum.shareit.exception;

import java.io.IOException;

public class UserNotAuthorizedException extends IOException {

    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
