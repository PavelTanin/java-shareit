package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Data
public class UserDto {

    private Long id;

    private String name;

    @Email(message = "Некорректно указано поле email")
    private String email;

}
