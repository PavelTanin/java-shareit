package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.EmptyRequestDescriptionException;
import ru.practicum.shareit.exception.IncorrectBookingTimeException;
import ru.practicum.shareit.exception.IncorrectItemValueException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class CustomValidator {

    public static void isBookingValid(BookingDto bookingDto) {
        if (bookingDto.getItemId() == null) {
            log.info("Некорректно указан id предмета");
            throw new IncorrectItemValueException("Некорректно указан id предмета");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd()) ||
                bookingDto.getEnd().truncatedTo(ChronoUnit.SECONDS)
                        .isBefore(bookingDto.getStart().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время окончания аренды раньше времени начала аренды");
            throw new IncorrectBookingTimeException("Дата окончания аренды не может быть раньше времени ее начала");
        }
    }

    public static void isRequestValid(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription().isEmpty()) {
            log.info("Попытка создать запрос на вещь с пустым описанием");
            throw new EmptyRequestDescriptionException("Описание не может быть пустым");
        }
    }

    public static void isCommentValid(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new IncorrectItemValueException("Нельзя так делать!");
        }
    }
}
