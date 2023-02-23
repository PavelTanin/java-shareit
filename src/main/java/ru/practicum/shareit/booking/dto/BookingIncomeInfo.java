package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class BookingIncomeInfo {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;

}
