package ru.practicum.shareit.exception;

public class WrongStatusSetException extends RuntimeException {

    public WrongStatusSetException(String message) {
        super(message);
    }
}
