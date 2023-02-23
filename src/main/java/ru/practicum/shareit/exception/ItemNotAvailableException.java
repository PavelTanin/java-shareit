package ru.practicum.shareit.exception;

import java.io.IOException;

public class ItemNotAvailableException extends IOException {

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
