package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto createItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос /items с телом {} и параметром userID:{}", itemDto, userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен PATCH-запрос /items/{} с телом {} и параметром userId:{}", itemId, itemDto, userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен DELETE-запрос /items/{} с параметром UserId: {}", itemId, userId);
        return itemService.deleteItem(itemId, userId);
    }

    @PostMapping("{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto,
                                 @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен POST-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return itemService.addComment(commentDto, itemId, userId);
    }

    @PatchMapping("{itemId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable(value = "itemId") Long itemId, @PathVariable(value = "commentId") Long commentId) {
        log.info("Получен PATCH-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return itemService.updateComment(commentDto, itemId, commentId, userId);
    }

    @DeleteMapping("{itemId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "itemId") Long itemId, @PathVariable(value = "commentId") Long commentId) {
        log.info("Получен DELETE-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return itemService.deleteComment(commentId, itemId, userId);
    }

    @GetMapping("{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен GET-запрос /items/{} от пользователя id: {}", itemId, userId);
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findUserAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                          @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /items?from={}&size={} с параметром userId:{}", from, size, userId);
        return itemService.findUserAllItems(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItemByNameAndDescription(@RequestParam(value = "text") String text,
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /items/search?from={}&size={} с параметром text: {}", from, size, text);
        return itemService.searchItemByNameAndDescription(text, from, size);
    }


}
