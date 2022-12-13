package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.OwnerIdAndUserIdException;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CustomValidator customValidator;

    @SneakyThrows
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Попытка добавить новый предмет");
        if (userId == 0) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Неавторизованные пользователи не могут добавлять новые предметы");
        }
        if (!userRepository.contains(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Незарегестрированные пользователи не могут добавлять новые предметы");
        }
        final Item item = ItemMapper.toItem(itemDto);
        customValidator.isItemValid(item);
        return ItemMapper.toItemDto(itemRepository.createItem(item, userId));
    }

    @SneakyThrows
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        log.info("Попытка обновить информацию о предмете id:{}", itemId);
        if (userId == 0) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Неавторизованные пользователи не могут обновлять информацию");
        }
        if (!userRepository.contains(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new UserNotAuthorizedException("Незарегестрированные пользователи не могут обновлять информацию");
        }
        if (!itemRepository.contains(itemId)) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Такой предмет отсутствует");
        }
        if (!itemRepository.getOwnerId(itemId).equals(userId)) {
            log.info("Пользователь пытается обновить информацию о чужом предмете");
            throw new OwnerIdAndUserIdException("Обновлять информацию о предмете могут только владельцы");
        }
        if (itemDto.getName() != null) {
            itemRepository.updateName(itemDto.getName(), itemId);
        }
        if (itemDto.getDescription() != null) {
            itemRepository.updateDescription(itemDto.getDescription(), itemId);
        }
        if (itemDto.getAvailable() != null) {
            itemRepository.updateAvailable(itemDto.getAvailable(), itemId);
        }
        log.info("Информация о предмете id:{} обновлена", itemId);
        return ItemMapper.toItemDto(itemRepository.findItemById(itemId));
    }

    @SneakyThrows
    @Override
    public ItemDto findItemById(Long itemId) {
        log.info("Попытка получить информацию о предмете id:{}", itemId);
        if (!itemRepository.contains(itemId)) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Предмет не найден");
        }
        return ItemMapper.toItemDto(itemRepository.findItemById(itemId));
    }

    @SneakyThrows
    @Override
    public List<ItemDto> findUserAllItems(Long userId) {
        if (userId == 0) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Пользователь не авторизован");
        }
        log.info("Попытка получить список всех предметов пользователя id:{}", userId);
        if (!userRepository.contains(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new UserNotAuthorizedException("Пользователь не зарегестрирован");
        }
        return itemRepository.findUserAllItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemByNameAndDescription(String text) {
        if (text == null || text.isBlank()) {
            log.info("В поисковой строке пусто, получен пустой список предметов");
            return Collections.emptyList();
        }
        log.info("Попытка получить список предметов, доступных для аренды, по запросу {}", text);
        return itemRepository.searchItemByNameAndDescription(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
