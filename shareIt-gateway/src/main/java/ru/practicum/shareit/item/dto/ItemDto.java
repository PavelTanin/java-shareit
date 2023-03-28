package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @NotEmpty(message = "Название не может быть пустым")
    private String name;

    @NotEmpty(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус предмета не может быть пустым")
    private Boolean available;

    private Long requestId;

}
