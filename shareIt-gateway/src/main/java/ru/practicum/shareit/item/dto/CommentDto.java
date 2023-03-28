package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CommentDto {

    @NotNull(message = "Комментарий должен содержать текст")
    @NotEmpty(message = "Комментарий должен содержать текст")
    private String text;
}
