package ru.practicum.shareit.exception;

public class NoCreatedItemsException extends RuntimeException {

    public NoCreatedItemsException(String message) {
        super(message);
    }
}
