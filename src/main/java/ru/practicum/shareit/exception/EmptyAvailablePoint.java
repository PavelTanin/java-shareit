package ru.practicum.shareit.exception;

public class EmptyAvailablePoint extends RuntimeException {

    public EmptyAvailablePoint(String message) {
        super(message);
    }
}
