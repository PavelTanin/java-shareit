package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private Long id;

    @Email(message = "Некорректно указано поле email")
    private String email;

    private String name;

    public UserDto(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
