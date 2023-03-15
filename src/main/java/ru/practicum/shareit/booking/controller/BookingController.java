package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                                    @RequestBody BookingIncomeInfo bookingDto) {
        log.info("Получен POST-запрос /bookings с телом {} и параметром userID: {}", bookingDto, userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                                     @PathVariable(value = "bookingId") Long bookingId,
                                     @RequestParam(value = "approved") String approved) {
        log.info("Получен PATCH-запрос /bookings/{}?approved={} с телом {} и параметром userID: {}", bookingId,
                approved, userId);
        return bookingService.changeBookingStatus(bookingId, approved, userId);
    }

    @DeleteMapping("{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                                @PathVariable(value = "bookingId") Long bookingId) {
        log.info("Получен DELETE-запрос /bookings/{} с параметром userId: {}", bookingId, userId);
        return bookingService.deleteBooking(bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto findById(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                               @PathVariable(value = "bookingId") Long bookingId) {
        log.info("Получен GET-запрос /bookings/{} с параметром userID: {}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findUserBookings(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /bookings?state={}&from={}&size={} с параметром userID: {}", state, from,
                size, userId);
        return bookingService.findUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findOwnerBookings(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") Long userId,
                                              @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                              @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /bookings/owner?state={}&from={}&size={} с параметром userID: {}", state,
                from, size, userId);
        return bookingService.findOwnerBookings(userId, state, from, size);
    }

}
