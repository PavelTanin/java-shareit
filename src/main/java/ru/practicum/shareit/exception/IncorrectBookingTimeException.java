package ru.practicum.shareit.exception;

import java.io.IOException;

public class IncorrectBookingTimeException extends IOException {

    public IncorrectBookingTimeException(String message) {
        super(message);
    }
}
