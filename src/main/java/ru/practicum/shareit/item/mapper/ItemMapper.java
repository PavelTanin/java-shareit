package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    public static ItemForBookingDto toItemForBookingDto(Item item) {
        return new ItemForBookingDto(item.getId(), item.getName());
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        ItemForRequestDto itemDto = new ItemForRequestDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner().getId());
        if(item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }
}
