package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.stream.Collectors;

public class RequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
        if (itemRequest.getItems() != null) {
            itemRequestDto.setItems(itemRequest.getItems().stream()
                    .map(ItemMapper::toItemForRequestDto)
                    .collect(Collectors.toList()));
        }
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), itemRequestDto.getCreated());
    }
}
