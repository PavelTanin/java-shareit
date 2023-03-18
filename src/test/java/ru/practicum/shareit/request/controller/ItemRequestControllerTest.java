package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createItemRequest() {
        Long userId = 1L;
        ItemRequestDto responseRequest = new ItemRequestDto(1L, "Test",
                LocalDateTime.now().minusSeconds(2));
        when(itemRequestService.createRequest(any(), anyLong())).thenReturn(responseRequest);


        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseRequest), result);
    }

    @SneakyThrows
    @Test
    void updateItemRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto responseRequest = new ItemRequestDto(1L, "Renamed",
                LocalDateTime.now().minusSeconds(2));
        when(itemRequestService.updateItemRequest(any(), anyLong(), anyLong())).thenReturn(responseRequest);


        String result = mockMvc.perform(patch("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseRequest), result);
    }

    @SneakyThrows
    @Test
    void deleteRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        String expectedResult = "Запрос успешно удален";
        when(itemRequestService.deleteItemRequest(anyLong(), anyLong())).thenReturn(expectedResult);

        String result = mockMvc.perform(delete("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .content("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedResult, result);
    }

    @SneakyThrows
    @Test
    void findRequestById() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemForRequestDto item1 = new ItemForRequestDto(1L, "Test", "Test", true,
                1L, 1L);
        ItemForRequestDto item2 = new ItemForRequestDto(2L, "Test2", "Test2", true,
                1L, 2L);
        ItemRequestDto responseRequest = new ItemRequestDto(requestId, "Test",
                LocalDateTime.now().minusDays(2), List.of(item1, item2));
        when(itemRequestService.findRequestById(anyLong(), anyLong())).thenReturn(responseRequest);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .content("application/json")
                        .content(objectMapper.writeValueAsString(responseRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseRequest), result);
    }

    @SneakyThrows
    @Test
    void findAllUserRequests() {
        Long userId = 1L;
        ItemForRequestDto item1 = new ItemForRequestDto(1L, "Test", "Test", true,
                1L, 2L);
        ItemForRequestDto item2 = new ItemForRequestDto(2L, "Test2", "Test2", true,
                1L, 3L);
        ItemForRequestDto item3 = new ItemForRequestDto(3L, "Test3", "Test3", true,
                2L, 2L);
        ItemRequestDto responseRequest1 = new ItemRequestDto(1L, "Test",
                LocalDateTime.now().minusDays(2), List.of(item1, item2));
        ItemRequestDto responseRequest2 = new ItemRequestDto(2L, "Test2",
                LocalDateTime.now().minusDays(1), List.of(item3));
        List<ItemRequestDto> expectedResult = List.of(responseRequest1, responseRequest2);
        when(itemRequestService.findAllUserRequests(anyLong())).thenReturn(expectedResult);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);

    }

   /* @SneakyThrows
    @Test
    void findAllRequests() {
        Long userId = 1L;
        ItemForRequestDto item1 = new ItemForRequestDto(1L, "Test", "Test", true,
                1L, 2L);
        ItemForRequestDto item2 = new ItemForRequestDto(2L, "Test2", "Test2", true,
                1L, 3L);
        ItemForRequestDto item3 = new ItemForRequestDto(3L, "Test3", "Test3", true,
                2L, 1L);
        ItemRequestDto responseRequest1 = new ItemRequestDto(1L, "Test",
                LocalDateTime.now().minusDays(2), List.of(item1, item2));
        ItemRequestDto responseRequest2 = new ItemRequestDto(2L, "Test2",
                LocalDateTime.now().minusDays(1), List.of(item3));
        List<ItemRequestDto> expectedResult = List.of(responseRequest1, responseRequest2);
        when(itemRequestService.findAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(expectedResult);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .content("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);
    }*/
}