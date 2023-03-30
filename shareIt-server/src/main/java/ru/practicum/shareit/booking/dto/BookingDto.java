package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserIdDto;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

    private UserIdDto booker;

    private ItemForBookingDto item;

    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, Status status, UserIdDto booker, ItemForBookingDto item) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }

    public BookingDto(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
}
