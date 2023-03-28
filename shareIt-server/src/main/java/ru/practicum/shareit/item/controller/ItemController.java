package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity createItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос /items с телом {} и параметром userID:{}", itemDto, userId);
        return new ResponseEntity(itemService.createItem(itemDto, userId), HttpStatus.OK);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен PATCH-запрос /items/{} с телом {} и параметром userId:{}", itemId, itemDto, userId);
        return new ResponseEntity(itemService.updateItem(itemDto, itemId, userId), HttpStatus.OK);
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity deleteItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен DELETE-запрос /items/{} с параметром UserId: {}", itemId, userId);
        return new ResponseEntity(new String[]{itemService.deleteItem(itemId, userId)}, HttpStatus.OK);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto,
                                 @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен POST-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return new ResponseEntity(itemService.addComment(commentDto, itemId, userId), HttpStatus.OK);
    }

    @PatchMapping("{itemId}/comment/{commentId}")
    public ResponseEntity updateComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable(value = "itemId") Long itemId, @PathVariable(value = "commentId") Long commentId) {
        log.info("Получен PATCH-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return new ResponseEntity(itemService.updateComment(commentDto, itemId, commentId, userId), HttpStatus.OK);
    }

    @DeleteMapping("{itemId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "itemId") Long itemId, @PathVariable(value = "commentId") Long commentId) {
        log.info("Получен DELETE-запрос /items/{}/comment от пользователя userId:{}", itemId, userId);
        return new ResponseEntity(new String[]{itemService.deleteComment(commentId, itemId, userId)}, HttpStatus.OK);
    }

    @GetMapping("{itemId}")
    public ResponseEntity findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "itemId") Long itemId) {
        log.info("Получен GET-запрос /items/{} от пользователя id: {}", itemId, userId);
        return new ResponseEntity(itemService.findItemById(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findUserAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                          @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /items?from={}&size={} с параметром userId:{}", from, size, userId);
        return new ResponseEntity(itemService.findUserAllItems(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity searchItemByNameAndDescription(@RequestParam(value = "text") String text,
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /items/search?from={}&size={} с параметром text: {}", from, size, text);
        return new ResponseEntity(itemService.searchItemByNameAndDescription(text, from, size), HttpStatus.OK);
    }


}
