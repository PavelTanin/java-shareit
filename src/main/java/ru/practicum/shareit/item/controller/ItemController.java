package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос /items с параметром userID:{}", userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable(value = "itemId") Long id,
                              @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId) {
        log.info("Получен PATCH-запрос /items/{} с параметром userId:{}", id, userId);
        return itemService.updateItem(itemDto, id, userId);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findItemById(@PathVariable(value = "itemId") Long id) {
        log.info("Получен GET-запрос /items/{}", id);
        return itemService.findItemById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findUserAllItems(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId) {
        log.info("Получен GET-запрос с параметром userId:{}", userId);
        return itemService.findUserAllItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItemByNameAndDescription(@RequestParam(value = "text") String text) {
        log.info("Получен GET-запрос с параметром text: {}", text);
        return itemService.searchItemByNameAndDescription(text);
    }


}
