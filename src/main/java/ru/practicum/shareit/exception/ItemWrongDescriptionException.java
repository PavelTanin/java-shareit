package ru.practicum.shareit.exception;

import java.io.IOException;

public class ItemWrongDescriptionException extends IOException {

    public ItemWrongDescriptionException(String message) {
        super(message);
    }
}
