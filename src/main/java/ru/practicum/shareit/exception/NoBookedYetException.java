package ru.practicum.shareit.exception;

import java.io.IOException;

public class NoBookedYetException extends IOException {

    public NoBookedYetException(String message) {
        super(message);
    }

}
