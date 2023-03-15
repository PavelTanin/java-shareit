package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserIdDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void createBooking() {
        BookingIncomeInfo requestBooking = new BookingIncomeInfo();
        requestBooking.setItemId(1L);
        requestBooking.setStart(LocalDateTime.of(2023, 12, 10, 17, 45));
        requestBooking.setEnd(LocalDateTime.of(2023, 12, 20, 17, 45));
        BookingDto responseBooking = new BookingDto(1L, requestBooking.getStart(),
                requestBooking.getEnd(), Status.WAITING, new UserIdDto(), new ItemForBookingDto());
        when(bookingService.createBooking(any(), anyLong())).thenReturn(responseBooking);


        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(responseBooking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseBooking), result);

    }

    @SneakyThrows
    @Test
    void approveBooking() {
        Long bookingId = 1L;
        BookingDto responseBooking = new BookingDto(1L, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(3), Status.APPROVED, new UserIdDto(), new ItemForBookingDto());
        when(bookingService.changeBookingStatus(anyLong(), anyString(), anyLong())).thenReturn(responseBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}?approved=", bookingId)
                .content("application/json")
                .content(objectMapper.writeValueAsString(responseBooking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseBooking), result);
    }

    @SneakyThrows
    @Test
    void deleteBooking() {
        Long bookingId = 1L;
        String expectedResult = "Заявка на аренду успешно удалена";
        when(bookingService.deleteBooking(anyLong(), anyLong())).thenReturn(expectedResult);

        String result = mockMvc.perform(delete("/bookings/{bookingId}", bookingId)
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
    void findById() {
        Long bookingId = 1L;
        UserIdDto bookerId = new UserIdDto(1L);
        ItemForBookingDto bookedItem = new ItemForBookingDto(1L, "Test");
        BookingDto responseBooking = new BookingDto(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(3));
        responseBooking.setId(bookingId);
        responseBooking.setStatus(Status.APPROVED);
        responseBooking.setBooker(bookerId);
        responseBooking.setItem(bookedItem);
        when(bookingService.findById(anyLong(), anyLong())).thenReturn(responseBooking);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .content("application/json")
                .content(objectMapper.writeValueAsString(responseBooking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseBooking), result);
    }

    @SneakyThrows
    @Test
    void findUserBookings() {
        Long userId = 1L;
        UserIdDto booker = new UserIdDto(userId);
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        ItemForBookingDto bookedItem1 = new ItemForBookingDto(1L, "Test");
        ItemForBookingDto bookedItem2 = new ItemForBookingDto(2L, "Test2");
        BookingDto responseBooking1 = new BookingDto(1L, start, end, Status.APPROVED, booker, bookedItem1);
        BookingDto responseBooking2 = new BookingDto(2L, start, end, Status.WAITING, booker, bookedItem2);
        List<BookingDto> expectedResult = List.of(responseBooking1, responseBooking2);
        when(bookingService.findUserBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(expectedResult);

        String result = mockMvc.perform(get("/bookings")
                .content("application/json")
                .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);

    }

    @SneakyThrows
    @Test
    void findOwnerBookings() {
        Long userId = 2L;
        UserIdDto booker = new UserIdDto(userId);
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        ItemForBookingDto bookedItem1 = new ItemForBookingDto(1L, "Test");
        ItemForBookingDto bookedItem2 = new ItemForBookingDto(2L, "Test2");
        BookingDto responseBooking1 = new BookingDto(1L, start, end, Status.APPROVED, booker, bookedItem1);
        BookingDto responseBooking2 = new BookingDto(2L, start, end, Status.WAITING, booker, bookedItem2);
        List<BookingDto> expectedResult = List.of(responseBooking1, responseBooking2);
        when(bookingService.findOwnerBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(expectedResult);

        String result = mockMvc.perform(get("/bookings/owner")
                        .content("application/json")
                        .content(objectMapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResult), result);
    }
}