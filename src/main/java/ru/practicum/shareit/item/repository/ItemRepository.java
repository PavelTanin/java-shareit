package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item, Long userId);

    void updateName(String name, Long itemId);

    void updateDescription(String description, Long itemId);

    void updateAvailable(Boolean available, Long itemId);

    Item findItemById(Long itemId);

    List<Item> findUserAllItems(Long userId);

    List<Item> searchItemByNameAndDescription(String text);

    boolean contains(Long itemId);

    Long getOwnerId(Long itemId);
}
