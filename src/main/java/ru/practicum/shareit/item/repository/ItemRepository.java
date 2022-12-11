package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long itemId, Long userId);

    Item findItemById(Long itemId);

    List<Item> findUserAllItems(Long userId);

    List<Item> searchItemByNameAndDescription(String text);
}
