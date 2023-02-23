package ru.practicum.shareit.exception;

import java.io.IOException;

public class BookingItemByOwnerException extends IOException {

    public BookingItemByOwnerException(String message) {
        super(message);
    }
}
