package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;

}
