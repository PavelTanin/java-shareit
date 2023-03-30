package ru.practicum.shareit.exception;

public class EmptyRequestDescriptionException extends RuntimeException {

    public EmptyRequestDescriptionException(String message) {
        super(message);
    }
}
