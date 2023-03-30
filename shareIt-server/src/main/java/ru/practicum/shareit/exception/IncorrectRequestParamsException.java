package ru.practicum.shareit.exception;

public class IncorrectRequestParamsException extends RuntimeException {

    public IncorrectRequestParamsException(String message) {
        super(message);
    }
}
