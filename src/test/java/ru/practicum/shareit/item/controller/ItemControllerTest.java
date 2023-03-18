package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void createItem() {
        Long userId = 1L;
        ItemDto requestItem = new ItemDto();
        requestItem.setName("Test");
        requestItem.setDescription("Test");
        requestItem.setAvailable(true);
        ItemDto responseItem = new ItemDto(1L, "Test", "Test", true);
        when(itemService.createItem(any(), anyLong())).thenReturn(responseItem);


        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseItem), result);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto requestItem = new ItemDto();
        requestItem.setId(itemId);
        requestItem.setName("Test");
        requestItem.setDescription("Test");
        requestItem.setAvailable(true);
        ItemDto responseItem = new ItemDto(1L, "Renamed", "Renamed", true);

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(responseItem);


        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseItem), result);
    }

    @SneakyThrows
    @Test
    void deleteItem() {
        Long userId = 1L;
        Long itemId = 1L;
        String expectedResult = "Предмет удален";
        when(itemService.deleteItem(anyLong(), anyLong())).thenReturn("Предмет удален");

        String result = mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedResult, result);

    }

    @SneakyThrows
    @Test
    void addComment() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto requestDto = new CommentDto();
        requestDto.setText("Test");
        CommentDto responseDto = new CommentDto(1L, "Test", "TestUser", LocalDateTime.now());
        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(responseDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDto), result);

    }

    @SneakyThrows
    @Test
    void updateComment() {
        Long userId = 1L;
        Long itemId = 1L;
        Long commentId = 1L;
        LocalDateTime created = LocalDateTime.now();
        CommentDto responseDto = new CommentDto(1L, "Renamed", "TestUser", created);
        when(itemService.updateComment(any(), anyLong(), anyLong(), anyLong())).thenReturn(responseDto);

        String result = mockMvc.perform(patch("/items/{itemId}/comment/{commentId}", itemId, commentId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDto), result);

    }

    @SneakyThrows
    @Test
    void deleteComment() {
        Long itemId = 1L;
        Long commentId = 1L;
        Long userId = 1L;
        String expectedResult = "Комментарий удален";
        when(itemService.deleteComment(anyLong(), anyLong(), anyLong())).thenReturn(expectedResult);

        String result = mockMvc.perform(delete("/items/{itemId}/comment/{commentId}", itemId, commentId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedResult, result);
    }

    @SneakyThrows
    @Test
    void findItemById() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto expectedResult = new ItemDto(1L, "Test", "Test", true);
        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(expectedResult);


        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);
    }

    @SneakyThrows
    @Test
    void findUserAllItems() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        ItemDto responseItem1 = new ItemDto(userId, "Test", "Test", true);
        ItemDto responseItem2 = new ItemDto(userId, "Test2", "Test2", false);
        List<ItemDto> expectedResult = List.of(responseItem1, responseItem2);
        when(itemService.findUserAllItems(anyLong(), anyInt(), anyInt())).thenReturn(expectedResult);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);
    }

    @SneakyThrows
    @Test
    void searchItemByNameAndDescription() {
        Long userId = 1L;
        ItemDto responseDto1 = new ItemDto(1L, "Test", "Test", true);
        ItemDto responseDto2 = new ItemDto(2L, "Test2", "Test2", false);
        List<ItemDto> expectedResult = List.of(responseDto1, responseDto2);
        when(itemService.searchItemByNameAndDescription(any(), anyInt(), anyInt())).thenReturn(List.of(responseDto1, responseDto2));

        String result = mockMvc.perform(get("/items/search?text=")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);
    }
}