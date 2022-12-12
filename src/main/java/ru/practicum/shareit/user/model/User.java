package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    @Email(message = "Некорректно указано поле email")
    private String email;

}
