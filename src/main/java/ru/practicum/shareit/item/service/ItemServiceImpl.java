package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import java.util.ArrayList;
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
        if(!userRepository.contains(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Нет такого пользователя");
        }
        Item item = ItemMapper.toItem(itemDto);
        customValidator.isItemValid(item);
        return ItemMapper.toItemDto(itemRepository.createItem(item, userId));
    }

    @SneakyThrows
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        log.info("Попытка обновить информацию о предмете id:{}", itemId);
        if (userId == 0) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Неавторизованные пользователи не могут добавлять новые предметы");
        }
        if(!userRepository.contains(userId)) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Неавторизованные пользователи не могут добавлять новые предметы");
        }
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.updateItem(item, itemId, userId));
    }

    @SneakyThrows
    @Override
    public ItemDto findItemById(Long itemId) {
        log.info("Попытка получить информацию о предмете id:{}", itemId);
        return ItemMapper.toItemDto(itemRepository.findItemById(itemId));
    }

    @SneakyThrows
    @Override
    public List<ItemDto> findUserAllItems(Long userId) {
        if (userId == 0) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Неавторизованные пользователи не могут добавлять новые предметы");
        }
        log.info("Попытка получить список всех предметов пользователя id:{}", userId);
        if(!userRepository.contains(userId)) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Неавторизованные пользователи не могут добавлять новые предметы");
        }
        return itemRepository.findUserAllItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemByNameAndDescription(String text) {
        if (text.isEmpty() || text == null) {
            log.info("В поисковой строке пусто, получен пустой список предметов");
            return new ArrayList<>();
        }
        log.info("Попытка получить список предметов, доступных для аренды, по запросу {}", text);
        return itemRepository.searchItemByNameAndDescription(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
