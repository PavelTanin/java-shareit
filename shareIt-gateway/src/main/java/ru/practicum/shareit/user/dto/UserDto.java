package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull(message = "email не может быть пустым")
    @NotEmpty(message = "email не может быть пустым")
    @Email(message = "Некорректно указан email")
    private String email;

    @NotNull(message = "email не может быть пустым")
    @NotEmpty(message = "email не может быть пустым")
    private String name;
}
