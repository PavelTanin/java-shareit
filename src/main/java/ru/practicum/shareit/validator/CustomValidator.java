package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Slf4j
@Component
public class CustomValidator {

    public void isUserValid(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            log.info("Поле name пустое");
            throw new UserEmptyNameException("Некорректно заполнено поле email");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || userDto.getEmail().isEmpty()) {
            log.info("Некоректно заполнено поле email - пустое или с пробелами");
            throw new UserEmailValidationException("Некорректно заполнено поле email");
        }
    }

    public void isItemValid(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.info("Некоректно заполнено название");
            throw new ItemWrongNameException("Название не может быть пустыми");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.info("Некоректно заполнено описание");
            throw new ItemWrongDescriptionException("Описание не может быть пустыми");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Не указана доступность предмета");
            throw new EmptyAvailablePoint("Не указано доступность предмета");
        }
    }

    public void isBookingValid(BookingIncomeInfo bookingIncomeInfo) {
        if (bookingIncomeInfo.getItemId() == null) {
            log.info("Некорректно указан id предмета");
            throw new IncorrectItemValueException("Некорректно указан id предмета");
        }
        if (bookingIncomeInfo.getStart() == null || bookingIncomeInfo.getStart().truncatedTo(ChronoUnit.SECONDS)
                .isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время начала аренды раньше текущего времени");
            throw new IncorrectBookingTimeException("Время начала аренды не может быть раньше текущего времени");
        }
        if (bookingIncomeInfo.getEnd() == null || bookingIncomeInfo.getEnd().truncatedTo(ChronoUnit.SECONDS)
                .isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время окончания аренды раньше текущего времени");
            throw new IncorrectBookingTimeException("Время окончания аренды не может быть раньше текущего времени");
        }
        if (bookingIncomeInfo.getStart().equals(bookingIncomeInfo.getEnd()) ||
                bookingIncomeInfo.getEnd().truncatedTo(ChronoUnit.SECONDS)
                .isBefore(bookingIncomeInfo.getStart().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время окончания аренды раньше времени начала аренды");
            throw new IncorrectBookingTimeException("Дата окончания аренды не может быть раньше времени ее начала");
        }

    }

    public void isRequestValid(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            log.info("Попытка создать запрос на вещь с пустым описанием");
            throw new EmptyRequestDescriptionException("Описание не может быть пустым");
        }
    }

    public void isPageableParamsCorrect(Integer from, Integer size) {
        if (from < 0) {
            log.info("Получено отрицательное значение from: {}", from);
            throw new IncorrectRequestParamsException("Такой страницы не существует");
        }
        if (size <= 0) {
            log.info("Получено некорректное значение size: {}", size);
            throw new IncorrectRequestParamsException("Невозможно отразить на странице отрицательное количество записей");
        }
    }

    public void isCommentValid(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            log.info("Комментарий не содержит текст");
            throw new EmptyCommentTextException("Текст комментария не может быть пустым");
        }
    }
}
