package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingIncomeInfo bookingDto, Long userId);

    BookingDto changeBookingStatus(Long bookingId, String isApproved, Long userId);

    String deleteBooking(Long bookingId, Long userId);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findUserBookings(Long userId, String state);

    List<BookingDto> findOwnerBookings(Long userId, String state);
}
