package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
												@RequestBody @Valid BookingDto bookingDto) {
		log.info("Получен POST-запрос /bookings с телом {} и параметром userID: {}", bookingDto, userId);
		return bookingClient.createBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(value = "X-Sharer-User-Id") long userId,
									 @PathVariable(value = "bookingId") Long bookingId,
									 @RequestParam(value = "approved") String approved) {
		log.info("Получен PATCH-запрос /bookings/{}?approved={} с телом {} и параметром userID: {}", bookingId,
				approved, userId);
		return bookingClient.changeBookingStatus(bookingId, approved, userId);
	}

	@DeleteMapping("{bookingId}")
	public ResponseEntity<Object> deleteBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
								@PathVariable(value = "bookingId") Long bookingId) {
		log.info("Получен DELETE-запрос /bookings/{} с параметром userId: {}", bookingId, userId);
		return bookingClient.deleteBooking(bookingId, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
										   @PathVariable(value = "bookingId") Long bookingId) {
		log.info("Получен GET-запрос /bookings/{} с параметром userID: {}", bookingId, userId);
		return bookingClient.findById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> findUserBookings(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
												   @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
												   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
												   @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
		log.info("Получен GET-запрос /bookings?state={}&from={}&size={} с параметром userID: {}", state, from,
				size, userId);
		return bookingClient.findUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findOwnerBookings(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
													@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
													@RequestParam(value = "from", defaultValue = "0") Integer from,
													@RequestParam(value = "size", defaultValue = "10") Integer size) {
		log.info("Получен GET-запрос /bookings/owner?state={}&from={}&size={} с параметром userID: {}", state,
				from, size, userId);
		return bookingClient.findOwnerBookings(userId, state, from, size);
	}



	/*@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}*/














}
