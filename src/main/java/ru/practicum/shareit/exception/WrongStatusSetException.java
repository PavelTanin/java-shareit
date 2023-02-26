package ru.practicum.shareit.exception;

import java.io.IOException;

public class WrongStatusSetException extends IOException {

    public WrongStatusSetException(String message) {
        super(message);
    }
}
