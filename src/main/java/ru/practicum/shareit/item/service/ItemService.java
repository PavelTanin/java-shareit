package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto findItemById(Long itemId);

    List<ItemDto> findUserAllItems(Long userId);

    List<ItemDto> searchItemByNameAndDescription(String text);
}
