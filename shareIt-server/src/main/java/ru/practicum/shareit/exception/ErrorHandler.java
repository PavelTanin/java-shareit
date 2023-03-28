package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, UserNotAuthorizedException.class, ItemNotAvailableException.class,
            WrongStatusSetException.class, EmptyCommentTextException.class,
            NoBookedYetException.class, EmptyRequestDescriptionException.class,
            IncorrectRequestParamsException.class})
    public void handleBadRequest(HttpServletResponse response, Exception e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler({ObjectNotFoundException.class, OwnerIdAndUserIdException.class,
                        BookedByOwnerException.class})
    public void handleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

}
