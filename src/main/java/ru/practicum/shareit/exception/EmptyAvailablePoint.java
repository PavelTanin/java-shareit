package ru.practicum.shareit.exception;

import java.io.IOException;

public class EmptyAvailablePoint extends IOException {

    public EmptyAvailablePoint(String message) {
        super(message);
    }
}
