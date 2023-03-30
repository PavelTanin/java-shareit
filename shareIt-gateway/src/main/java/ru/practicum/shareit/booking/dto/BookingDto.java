package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

	private Long itemId;

	@NotNull(message = "Поле не может быть пустым")
	@FutureOrPresent(message = "Время начала аренды не может быть раньше текущего времени")
	private LocalDateTime start;

	@NotNull(message = "Поле не может быть пустым")
	@Future(message = "Время окончания аренды не может быть раньше текущего времени")
	private LocalDateTime end;
}
