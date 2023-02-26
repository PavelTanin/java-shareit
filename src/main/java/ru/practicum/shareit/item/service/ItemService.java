package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    String deleteItem(Long itemId, Long userId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    CommentDto updateComment(CommentDto commentDto, Long itemId, Long commentId, Long userId);

    String deleteComment(Long commentId, Long itemId, Long userId);

    ItemDto findItemById(Long itemId, Long userId);

    List<ItemDto> findUserAllItems(Long userId);

    List<ItemDto> searchItemByNameAndDescription(String text);
}
