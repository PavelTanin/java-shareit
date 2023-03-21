package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getStatus(), UserMapper.userIdDto(booking.getBooker()), ItemMapper.toItemForBookingDto(booking.getItem()));
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingForItemDto(booking.getId(), booking.getBooker().getId(), booking.getStart(),
                    booking.getEnd());
        }
    }

}
