package ru.practicum.shareit.exception;

import java.io.IOException;

public class CrossStartOrEndTimeException extends IOException {

    public CrossStartOrEndTimeException(String message) {
        super(message);
    }
}
