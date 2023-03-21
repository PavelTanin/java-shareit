package ru.practicum.shareit.exception;

public class ItemWrongNameException extends RuntimeException {

    public ItemWrongNameException(String message) {
        super(message);
    }
}
