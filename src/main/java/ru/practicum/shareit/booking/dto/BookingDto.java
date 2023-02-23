package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserIdDto;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
public class BookingDto {

    private Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime start;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime end;

    private Status status;

    private UserIdDto booker;

    private ItemBookingDto item;

    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, Status status, UserIdDto booker, ItemBookingDto item) {
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
