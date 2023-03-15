package ru.practicum.shareit.exception;

public class NoCreatedBookingsException extends RuntimeException {

    public NoCreatedBookingsException(String message) {
        super(message);
    }
}
