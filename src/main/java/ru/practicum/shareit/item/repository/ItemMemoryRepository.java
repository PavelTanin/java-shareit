package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemMemoryRepository implements ItemRepository {

    private final Map<Long, Item> itemStorage = new HashMap<>();

    private final Map<Long, Set<Long>> owners = new HashMap<>();

    private Long id = 0L;

    @Override
    public Item createItem(Item item, Long userId) {
        item.setId(getId());
        item.setOwner(userId);
        itemStorage.put(item.getId(), item);
        log.info("Пользователь id:{} добавил новый предмет id:{}", userId, item.getId());
        return item;
    }

    @Override
    public void updateName(String name, Long itemId) {
        log.info("Изменено название предмета id:{}", itemId);
        itemStorage.get(itemId).setName(name);
    }

    @Override
    public void updateDescription(String description, Long itemId) {
        log.info("Изменено описание предмета id:{}", itemId);
        itemStorage.get(itemId).setDescription(description);
    }

    @Override
    public void updateAvailable(Boolean available, Long itemId) {
        log.info("Изменена доступность предмета id:{}", itemId);
        itemStorage.get(itemId).setAvailable(available);
    }

    @Override
    public Item findItemById(Long itemId) {
        log.info("Получена информация о предмете id:{}", itemId);
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> findUserAllItems(Long userId) {
        log.info("Получен список предметов пользователя id:{}", userId);
        return itemStorage.values().stream().filter(o -> o.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemByNameAndDescription(String text) {
        log.info("Получен список доступных для аренды предметов по ключевому слову - {}", text);
        return itemStorage.values().stream()
                .filter(o -> o.getName().toLowerCase().contains(text) || o.getDescription().toLowerCase().contains(text))
                .filter(o -> o.getAvailable() == true)
                .collect(Collectors.toList());
    }

    private Long getId() {
        id++;
        return id;
    }

    @Override
    public boolean contains(Long itemId) {
        return itemStorage.containsKey(itemId);
    }

    @Override
    public Long getOwnerId(Long itemId) {
        return itemStorage.get(itemId).getOwner();
    }
}
