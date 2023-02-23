package ru.practicum.shareit.exception;

import java.io.IOException;

public class IncorrectStateParamException extends IOException {

    public IncorrectStateParamException(String message) {
        super(message);
    }
}
