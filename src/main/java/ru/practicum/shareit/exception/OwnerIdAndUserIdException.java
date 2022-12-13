package ru.practicum.shareit.exception;

import java.io.IOException;

public class OwnerIdAndUserIdException extends IOException {

    public OwnerIdAndUserIdException(String message) {
        super(message);
    }
}
