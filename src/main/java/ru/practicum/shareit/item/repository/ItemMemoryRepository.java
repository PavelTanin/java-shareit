package ru.practicum.shareit.item.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.OwnerIdAndUserIdException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemMemoryRepository implements ItemRepository {

    private final Map<Long, Item> itemStorage = new HashMap<>();

    private Long id = 0L;

    @Override
    public Item createItem(Item item, Long userId) {
        item.setId(getId());
        item.setOwner(userId);
        itemStorage.put(item.getId(), item);
        log.info("Добавлен новый предмет id:{}", id);
        return item;
    }

    @SneakyThrows
    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        if (!itemStorage.containsKey(itemId)) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Такой предмет отсутствует");
        }
        var updatedItem = itemStorage.get(itemId);
        if (!updatedItem.getOwner().equals(userId)) {
            log.info("Пользователь пытается обновить информацию о чужом предмете");
            throw new OwnerIdAndUserIdException("Обновлять информацию о предмете могут только владельцы");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        log.info("Обновлена информация о предмете id:{}", item.getId());
        return updatedItem;
    }

    @SneakyThrows
    @Override
    public Item findItemById(Long itemId) {
        if (!itemStorage.containsKey(itemId)) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Предмет не найден");
        }
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
        log.info("Получен список доступных для аренды предметов по запросу {}", text);
        return itemStorage.values().stream()
                .filter(o -> o.getName().toLowerCase().contains(text) || o.getDescription().toLowerCase().contains(text))
                .filter(o -> o.getAvailable() == true)
                .collect(Collectors.toList());
    }

    private Long getId() {
        id++;
        return id;
    }
}
