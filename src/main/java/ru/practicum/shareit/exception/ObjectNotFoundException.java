package ru.practicum.shareit.exception;

import java.io.IOException;

public class ObjectNotFoundException extends IOException {

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
