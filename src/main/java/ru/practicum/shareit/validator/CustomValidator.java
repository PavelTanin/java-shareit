package ru.practicum.shareit.validator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyAvailablePoint;
import ru.practicum.shareit.exception.ItemWrongNameException;
import ru.practicum.shareit.exception.UserEmailValidationException;
import ru.practicum.shareit.exception.UserEmptyNameException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Component
public class CustomValidator {

    @SneakyThrows
    public void isUserValid(User user) {
        if (user.getName().isEmpty() || user.getName() == null){
            log.info("Поле name пустое");
            throw new UserEmptyNameException("Некорректно заполнено поле email");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            log.info("Некоректно заполнено поле email - пустое или с пробелами");
            throw new UserEmailValidationException("Некорректно заполнено поле email");
        }
    }

    @SneakyThrows
    public void isItemValid(Item item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            log.info("Некоректно заполнено название");
            throw new ItemWrongNameException("Название не может быть пустыми");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            log.info("Некоректно заполнено описание");
            throw new ItemWrongNameException("Описание не может быть пустыми");
        }
        if (item.getAvailable() == null) {
            log.info("Не указана доступность предмета");
            throw new EmptyAvailablePoint("Не указано доступность предмета");
        }
    }
}
