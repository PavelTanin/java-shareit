package ru.practicum.shareit.exception;

public class BookedByOwnerException extends RuntimeException {

    public BookedByOwnerException(String message) {
        super(message);
    }
}
