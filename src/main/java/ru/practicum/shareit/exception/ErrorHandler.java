package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UserNameDuplicateException.class)
    public void handleConflict(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler({ValidationException.class, UserEmailValidationException.class, EmptyAvailablePoint.class,
            UserNotAuthorizedException.class, UserEmptyNameException.class,
            ItemWrongDescriptionException.class, ItemWrongNameException.class})
    public void handleBadRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({ObjectNotFoundException.class, OwnerIdAndUserIdException.class})
    public void handleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

}
