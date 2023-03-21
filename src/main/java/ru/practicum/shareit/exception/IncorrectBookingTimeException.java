package ru.practicum.shareit.exception;

public class IncorrectBookingTimeException extends RuntimeException {

    public IncorrectBookingTimeException(String message) {
        super(message);
    }
}
