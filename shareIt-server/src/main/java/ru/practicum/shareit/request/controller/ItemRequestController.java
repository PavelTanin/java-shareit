package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос /requests с параметром userId: {} и телом запроса: {}", userId, itemRequestDto);
        return new ResponseEntity(itemRequestService.createRequest(itemRequestDto, userId), HttpStatus.OK);

    }

    @PatchMapping("{requestId}")
    public ResponseEntity updateItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto,
                                            @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен PATCH-запрос /requests/{} с параметром userId: {} и телом запроса: {}",
                requestId, userId, itemRequestDto);
        return new ResponseEntity(itemRequestService.updateItemRequest(itemRequestDto, userId, requestId),
                HttpStatus.OK);
    }

    @DeleteMapping("{requestId}")
    public ResponseEntity deleteRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен DELETE-запрос /requests/{} с параметром userId: {}", requestId, userId);
        return new ResponseEntity(itemRequestService.deleteItemRequest(requestId, userId), HttpStatus.OK);
    }

    @GetMapping("{requestId}")
    public ResponseEntity findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                          @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен GET-запрос /requests/{} с параметром userId: {}", requestId, userId);
        return new ResponseEntity(itemRequestService.findRequestById(requestId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findAllUserRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /requests с параметром userId: {}", userId);
        return new ResponseEntity(itemRequestService.findAllUserRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity findAllRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /requests/all c параметрами from: {} и size: {} и " +
                "параметром userId: {}", from, size, userId);
        return new ResponseEntity(itemRequestService.findAllRequests(userId, from, size), HttpStatus.OK);
    }

}
