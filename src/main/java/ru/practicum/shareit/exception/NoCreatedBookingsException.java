package ru.practicum.shareit.exception;

import java.io.IOException;

public class NoCreatedBookingsException extends IOException {

    public NoCreatedBookingsException(String message) {
        super(message);
    }
}
