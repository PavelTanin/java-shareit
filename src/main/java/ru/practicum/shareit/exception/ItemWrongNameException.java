package ru.practicum.shareit.exception;

import java.io.IOException;

public class ItemWrongNameException extends IOException {

    public ItemWrongNameException(String message) {
        super(message);
    }
}
