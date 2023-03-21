package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingIncomeInfo bookingDto, Long userId);

    BookingDto changeBookingStatus(Long bookingId, String isApproved, Long userId);

    String deleteBooking(Long bookingId, Long userId);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findUserBookings(Long userId, BookingState state, Integer from, Integer size);

    List<BookingDto> findOwnerBookings(Long userId, BookingState state, Integer from, Integer size);
}
