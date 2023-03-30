package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос /requests с параметром userId: {} и телом запроса: {}", userId, itemRequestDto);
        return itemRequestClient.createRequest(itemRequestDto, userId);

    }

    @PatchMapping("{requestId}")
    public ResponseEntity<Object> updateItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto,
                                                    @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен PATCH-запрос /requests/{} с параметром userId: {} и телом запроса: {}",
                requestId, userId, itemRequestDto);
        return itemRequestClient.updateItemRequest(itemRequestDto, userId, requestId);
    }

    @DeleteMapping("{requestId}")
    public ResponseEntity<Object> deleteRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен DELETE-запрос /requests/{} с параметром userId: {}", requestId, userId);
        return itemRequestClient.deleteItemRequest(requestId, userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                  @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен GET-запрос /requests/{} с параметром userId: {}", requestId, userId);
        return itemRequestClient.findRequestById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /requests с параметром userId: {}", userId);
        return itemRequestClient.findAllUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /requests/all c параметрами from: {} и size: {} и " +
                "параметром userId: {}", from, size, userId);
        return itemRequestClient.findAllRequests(userId, from, size);
    }
}
