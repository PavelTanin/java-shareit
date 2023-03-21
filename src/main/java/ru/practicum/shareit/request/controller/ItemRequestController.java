package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос /requests с параметром userId: {} и телом запроса: {}", userId, itemRequestDto);
        return itemRequestService.createRequest(itemRequestDto, userId);

    }

    @PatchMapping("{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto updateItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto,
                                            @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен PATCH-запрос /requests/{} с параметром userId: {} и телом запроса: {}",
                requestId, userId, itemRequestDto);
        return itemRequestService.updateItemRequest(itemRequestDto, userId, requestId);
    }

    @DeleteMapping("{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен DELETE-запрос /requests/{} с параметром userId: {}", requestId, userId);
        return itemRequestService.deleteItemRequest(requestId, userId);
    }

    @GetMapping("{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long uesrId,
                                          @PathVariable(value = "requestId") Long requestId) {
        log.info("Получен GET-запрос /requests/{} с параметром userId: {}", requestId, uesrId);
        return itemRequestService.findRequestById(requestId, uesrId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findAllUserRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос /requests с параметром userId: {}", userId);
        return itemRequestService.findAllUserRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> findAllRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /requests/all c параметрами from: {} и size: {} и " +
                "параметром userId: {}", from, size, userId);
        return itemRequestService.findAllRequests(userId, from, size);
    }

}
