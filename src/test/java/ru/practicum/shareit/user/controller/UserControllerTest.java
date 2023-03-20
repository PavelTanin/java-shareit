package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @SneakyThrows
    @Test
    void createUser() {
        UserDto userDto = new UserDto("test@test.ru", "Test");
        String requestBody = objectMapper.writeValueAsString(userDto);
        UserDto returnedResult = userDto;
        returnedResult.setId(1L);
        when(userService.createUser(any())).thenReturn(returnedResult);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnedResult), result);
        verify(userService, times(1))
                .createUser(any());
    }

    @SneakyThrows
    @Test
    void updateUser() {
        Long userId = 1L;
        UserDto userDto = new UserDto(1L,"test@test.ru", "Test");
        String requestBody = objectMapper.writeValueAsString(userDto);
        userDto.setName("Renamed");
        userDto.setEmail("Renamed@test.ru");
        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService, times(1))
                .updateUser(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Long userId = 1L;
        String returnedResult = "Пользователь " + userId + " удален";
        when(userService.deleteUser(anyLong())).thenReturn(returnedResult);

        String result = mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(returnedResult, result);
        verify(userService, times(1))
                .deleteUser(anyLong());



    }

    @SneakyThrows
    @Test
    void findUserById() {
        Long userId = 1L;
        UserDto returnedResult = new UserDto(userId, "test@test.ru", "Test");
        when(userService.findUserById(anyLong())).thenReturn(returnedResult);

        String result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnedResult), result);
        verify(userService, times(1))
                .findUserById(anyLong());
    }


    @SneakyThrows
    @Test
    void findAllUsers() {
        UserDto userDto1 = new UserDto(1L, "test@mail.ru", "Test");
        UserDto userDto2 = new UserDto(2L, "test2@mail.ru", "Test2");
        List<UserDto> returnedResult = List.of(userDto1, userDto2);
        when(userService.findAllUsers()).thenReturn(returnedResult);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnedResult), result);
        verify(userService, times(1)).findAllUsers();
    }
}