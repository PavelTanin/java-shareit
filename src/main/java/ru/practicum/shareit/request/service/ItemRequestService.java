package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long userId, Long requestId);

    String deleteItemRequest(Long requestId, Long userId);

    ItemRequestDto findRequestById(Long requestId, Long userId);

    List<ItemRequestDto> findAllUserRequests(Long userId);

    List<ItemRequestDto> findAllRequests(Long userId, Integer from, Integer size);



}
