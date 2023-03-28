package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Получен POST-запрос /items с телом {} и параметром userID:{}", itemDto, userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен PATCH-запрос /items/{} с телом {} и параметром userId:{}", itemId, itemDto, userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен DELETE-запрос /items/{} с параметром UserId: {}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid CommentDto commentDto,
                                             @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен POST-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return itemClient.addComment(commentDto, itemId, userId);
    }

    @PatchMapping("{itemId}/comment/{commentId}")
    public ResponseEntity<Object> updateComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid CommentDto commentDto,
                                                @PathVariable(value = "itemId") Long itemId,
                                                @PathVariable(value = "commentId") Long commentId) {
        log.info("Получен PATCH-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return itemClient.updateComment(commentDto, itemId, commentId, userId);
    }

    @DeleteMapping("{itemId}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @PathVariable(value = "itemId") Long itemId,
                                                @PathVariable(value = "commentId") Long commentId) {
        log.info("Получен DELETE-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return itemClient.deleteComment(commentId, itemId, userId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                               @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен GET-запрос /items/{} от пользователя id: {}", itemId, userId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /items?from={}&size={} с параметром userId:{}", from, size, userId);
        return itemClient.findUserAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByNameAndDescription(@RequestParam(value = "text") String text,
                                                                 @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                 @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /items/search?from={}&size={} с параметром text: {}", from, size, text);
        return itemClient.searchItemByNameAndDescription(text, from, size);
    }

}
