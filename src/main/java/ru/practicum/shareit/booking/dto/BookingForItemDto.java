package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookingForItemDto {

    private Long id;
    private Long bookerId;

    public BookingForItemDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
