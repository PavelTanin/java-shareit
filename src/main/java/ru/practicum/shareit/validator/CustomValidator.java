package ru.practicum.shareit.validator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Slf4j
@Component
public class CustomValidator {

    @SneakyThrows
    public void isUserValid(UserDto userDto) {
        if (userDto.getName().isEmpty() || userDto.getName() == null) {
            log.info("Поле name пустое");
            throw new UserEmptyNameException("Некорректно заполнено поле email");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || userDto.getEmail().isEmpty()) {
            log.info("Некоректно заполнено поле email - пустое или с пробелами");
            throw new UserEmailValidationException("Некорректно заполнено поле email");
        }
    }

    @SneakyThrows
    public void isItemValid(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.info("Некоректно заполнено название");
            throw new ItemWrongNameException("Название не может быть пустыми");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.info("Некоректно заполнено описание");
            throw new ItemWrongNameException("Описание не может быть пустыми");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Не указана доступность предмета");
            throw new EmptyAvailablePoint("Не указано доступность предмета");
        }
    }

    @SneakyThrows
    public void isBookingValid(BookingIncomeInfo bookingIncomeInfo) {
        if (bookingIncomeInfo.getStart().truncatedTo(ChronoUnit.SECONDS)
                .isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время начала аренды раньше текущего времени");
            throw new IncorrectBookingTimeException("Время начала аренды не может быть раньше текущего времени");
        }
        if (bookingIncomeInfo.getEnd().truncatedTo(ChronoUnit.SECONDS)
                .isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время окончания аренды раньше текущего времени");
            throw new IncorrectBookingTimeException("Время окончания аренды не может быть раньше текущего времени");
        }
        if (bookingIncomeInfo.getEnd().truncatedTo(ChronoUnit.SECONDS)
                .isBefore(bookingIncomeInfo.getStart().truncatedTo(ChronoUnit.SECONDS))) {
            log.info("Время окончания аренды раньше времени начала аренды");
            throw new IncorrectBookingTimeException("Дата окончания аренды не может быть раньше времени ее начала");
        }

    }
}
