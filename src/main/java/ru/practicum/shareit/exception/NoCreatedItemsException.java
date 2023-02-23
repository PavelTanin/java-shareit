package ru.practicum.shareit.exception;

import java.io.IOException;

public class NoCreatedItemsException extends IOException {

    public NoCreatedItemsException(String message) {
        super(message);
    }
}
