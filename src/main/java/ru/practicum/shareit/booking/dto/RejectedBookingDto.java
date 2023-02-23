package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserIdDto;

@NoArgsConstructor
@Data
public class RejectedBookingDto extends BookingDto {

    private Long id;

    private Status status;

    private UserIdDto booker;

    private ItemBookingDto item;

    public RejectedBookingDto(Long id, Status status, UserIdDto booker, ItemBookingDto item) {
        this.id = id;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }
}
