package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, UserEmailValidationException.class, EmptyAvailablePoint.class,
            UserNotAuthorizedException.class, UserEmptyNameException.class,
            ItemWrongDescriptionException.class, ItemWrongNameException.class, ItemNotAvailableException.class,
            IncorrectBookingTimeException.class, WrongStatusSetException.class, EmptyCommentTextException.class,
            NoBookedYetException.class, IncorrectItemValueException.class, EmptyRequestDescriptionException.class,
            IncorrectIdException.class, IncorrectRequestParamsException.class})
    public void handleBadRequest(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({ObjectNotFoundException.class, OwnerIdAndUserIdException.class,
                        BookedByOwnerException.class})
    public void handleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestWithMessage(HttpServletResponse response,
                                                           MethodArgumentTypeMismatchException exception) throws IOException {
        return Map.of("error","Unknown state: " + exception.getValue());
    }

}
