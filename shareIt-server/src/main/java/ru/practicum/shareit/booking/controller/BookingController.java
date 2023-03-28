package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity createBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestBody BookingIncomeInfo bookingDto) {
        log.info("Получен POST-запрос /bookings с телом {} и параметром userID: {}", bookingDto, userId);
        return new ResponseEntity(bookingService.createBooking(bookingDto, userId), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity approveBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                     @PathVariable(value = "bookingId") Long bookingId,
                                     @RequestParam(value = "approved") String approved) {
        log.info("Получен PATCH-запрос /bookings/{}?approved={} с телом {} и параметром userID: {}", bookingId,
                approved, userId);
        return new ResponseEntity(bookingService.changeBookingStatus(bookingId, approved, userId),
                HttpStatus.OK);
    }

    @DeleteMapping("{bookingId}")
    public ResponseEntity deleteBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable(value = "bookingId") Long bookingId) {
        log.info("Получен DELETE-запрос /bookings/{} с параметром userId: {}", bookingId, userId);
        return new ResponseEntity(new String[]{bookingService.deleteBooking(bookingId, userId)}, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                               @PathVariable(value = "bookingId") Long bookingId) {
        log.info("Получен GET-запрос /bookings/{} с параметром userID: {}", bookingId, userId);
        return new ResponseEntity(bookingService.findById(bookingId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findUserBookings(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /bookings?state={}&from={}&size={} с параметром userID: {}", state, from,
                size, userId);
        return new ResponseEntity(bookingService.findUserBookings(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity findOwnerBookings(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                              @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /bookings/owner?state={}&from={}&size={} с параметром userID: {}", state,
                from, size, userId);
        return new ResponseEntity(bookingService.findOwnerBookings(userId, state, from, size), HttpStatus.OK);
    }

}
