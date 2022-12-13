package ru.practicum.shareit.exception;

import java.io.IOException;

public class UserEmptyNameException extends IOException {

    public UserEmptyNameException(String message) {
        super(message);
    }
}
