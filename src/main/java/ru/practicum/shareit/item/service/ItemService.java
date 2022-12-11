package ru.practicum.shareit.item.service;

import lombok.SneakyThrows;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    @SneakyThrows
    ItemDto findItemById(Long itemId);

    @SneakyThrows
    List<ItemDto> findUserAllItems(Long userId);

    List<ItemDto> searchItemByNameAndDescription(String text);
}
