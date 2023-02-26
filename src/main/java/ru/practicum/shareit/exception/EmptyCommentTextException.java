package ru.practicum.shareit.exception;

import java.io.IOException;

public class EmptyCommentTextException extends IOException {

    public EmptyCommentTextException(String message) {
        super(message);

    }
}
