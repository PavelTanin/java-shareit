package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        User booker = new User(1L, "Test", "test@test.ru");
        userRepository.save(booker);
        User owner = new User(2L, "Test2", "test2@test.ru");
        userRepository.save(owner);
        Item item = new Item(1L, "Test", "Test",
                true, owner, null);
        itemRepository.save(item);
        Booking currentBooking = new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), Status.APPROVED, booker, item);
        Booking pastBooking = new Booking(2L, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1), Status.APPROVED, booker, item);
        Booking futureBooking = new Booking(3L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), Status.APPROVED, booker, item);
        Booking waitingBooking = new Booking(4L, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), Status.WAITING, booker, item);
        Booking rejectedBooking = new Booking(5L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3), Status.REJECTED, booker, item);
        bookingRepository.saveAll(List.of(currentBooking, pastBooking, futureBooking, waitingBooking,
                rejectedBooking));
    }

    @Test
    void getBookerId() {
        Long bookerId = bookingRepository.getBookerId(1L);

        assertEquals(bookerId, 1L);
    }

    @Test
    void findAllUsersBookings() {
        List<Booking> bookings = bookingRepository
                .findAllUsersBookings(1L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 5),
                () -> assertEquals(bookings.get(1).getId(), 4L)
        );
    }

    @Test
    void findCurrentUserBookings() {
        List<Booking> bookings = bookingRepository
                .findCurrentUserBookings(1L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 1L)
        );
    }

    @Test
    void findPastUserBookings() {
        List<Booking> bookings = bookingRepository
                .findPastUserBookings(1L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 2L)
        );
    }

    @Test
    void findFutureUserBookings() {
        List<Booking> bookings = bookingRepository
                .findFutureUserBookings(1L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 3),
                () -> assertEquals(bookings.get(2).getId(), 3L)
        );
    }

    @Test
    void findWaitingUserBookings() {
        List<Booking> bookings = bookingRepository
                .findWaitingUserBookings(1L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 4L)
        );
    }

    @Test
    void findRejectedUserBookings() {
        List<Booking> bookings = bookingRepository
                .findRejectedUserBookings(1L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 5L)
        );
    }

    @Test
    void findAllOwnerBookings() {
        List<Booking> bookings = bookingRepository
                .findAllOwnerBookings(List.of(1L), PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 5),
                () -> assertEquals(bookings.get(0).getId(), 5L)
        );
    }

    @Test
    void findCurrentOwnerBookings() {
        List<Booking> bookings = bookingRepository
                .findCurrentOwnerBookings(List.of(1L), PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 1L)
        );
    }

    @Test
    void findPastOwnerBookings() {
        List<Booking> bookings = bookingRepository
                .findPastOwnerBookings(List.of(1L), PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 2L)
        );
    }

    @Test
    void findFutureOwnerBookings() {
        List<Booking> bookings = bookingRepository
                .findFutureOwnerBookings(List.of(1L), PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 3),
                () -> assertEquals(bookings.get(0).getId(), 5L)
        );
    }

    @Test
    void findWaitingOwnerBookings() {
        List<Booking> bookings = bookingRepository
                .findWaitingOwnerBookings(List.of(1L), PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 4L)
        );
    }

    @Test
    void findRejectedOwnerBookings() {
        List<Booking> bookings = bookingRepository
                .findRejectedOwnerBookings(List.of(1L), PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(bookings.size(), 1),
                () -> assertEquals(bookings.get(0).getId(), 5L)
        );
    }

    @Test
    void bookingsBeforeNowCount() {
        Long bookingBeforeNow = bookingRepository
                .bookingsBeforeNowCount(1L, 1L, LocalDateTime.now());

        assertEquals(bookingBeforeNow, 2L);

    }

    @Test
    void findAllByItemInAndStatusIsOrderByIdAsc() {
        List<Booking> bookings = bookingRepository
                .findAllByItemInAndStatusIsOrderByIdAsc(List.of(new Item(1L,
                        "Test", "Test", true,
                        new User(2L, "Test2", "test2@test.ru"),
                        null)), Status.APPROVED);

        assertAll(
                () -> assertEquals(bookings.size(), 3),
                () -> assertEquals(bookings.get(0).getId(), 1L)
        );
    }
}