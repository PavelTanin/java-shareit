package ru.practicum.shareit.exception;

public class NoBookedYetException extends RuntimeException {

    public NoBookedYetException(String message) {
        super(message);
    }

}
