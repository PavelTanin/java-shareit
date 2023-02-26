package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class BookingForItemDto {

    private Long id;
    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    public BookingForItemDto(Long id, Long bookerId, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.bookerId = bookerId;
        this.start = start;
        this.end = end;
    }
}
