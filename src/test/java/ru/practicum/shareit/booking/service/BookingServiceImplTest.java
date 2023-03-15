package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomValidator customValidator;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingWhenItemIsAvailableThenCreateBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingIncomeInfo creatBooking = new BookingIncomeInfo();
        creatBooking.setStart(LocalDateTime.of(2023, 3, 10, 17, 45));
        creatBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        creatBooking.setItemId(1L);
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test");
        Booking booking = new Booking(null,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.WAITING, user, item);
        BookingDto expectedResult = BookingMapper.toBookingDto(booking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.isAvailable(itemId)).thenReturn(true);
        when(itemRepository.getOwnerId(itemId)).thenReturn(2L);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.createBooking(creatBooking, userId);

        assertEquals(expectedResult, result);
    }

    @Test
    void createBookingWhenItemNotAvailableThenThrowNotAvailableException() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingIncomeInfo createBooking = new BookingIncomeInfo();
        createBooking.setStart(LocalDateTime.of(2023, 3, 10, 17, 45));
        createBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        createBooking.setItemId(1L);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.isAvailable(itemId)).thenReturn(false);

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(createBooking, userId));

        verify(itemRepository, never()).getOwnerId(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWhenItemNotValidThenThrowIncorrectTimeException() {
        Long userId = 1L;
        BookingIncomeInfo createBooking = new BookingIncomeInfo();
        createBooking.setStart(LocalDateTime.of(2023, 3, 19, 17, 45));
        createBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        createBooking.setItemId(1L);
        doThrow(IncorrectBookingTimeException.class).when(customValidator).isBookingValid(createBooking);

        assertThrows(IncorrectBookingTimeException.class,
                () -> bookingService.createBooking(createBooking, userId));

        verify(itemRepository, never()).isAvailable(anyLong());
        verify(itemRepository, never()).getOwnerId(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWhenUserIsOwnerThenThrowBookedByOwnerException() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingIncomeInfo createBooking = new BookingIncomeInfo();
        createBooking.setStart(LocalDateTime.of(2023, 3, 19, 17, 45));
        createBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        createBooking.setItemId(1L);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.isAvailable(itemId)).thenReturn(true);
        when(itemRepository.getOwnerId(itemId)).thenReturn(1L);

        assertThrows(BookedByOwnerException.class,
                () -> bookingService.createBooking(createBooking, userId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void changeBookingStatusWhenStatusChangeToApprovedThenSaveStatus() {
        Long bookingId = 1L;
        Long userId = 1L;
        String isApproved = "true";
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(1L);
        item.setName("Test");
        Booking booking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.WAITING, user, item);
        Booking updatedBooking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, user, item);
        BookingDto expectedResult = BookingMapper.toBookingDto(updatedBooking);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getOwnerId(anyLong())).thenReturn(1L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(booking);
        when(bookingRepository.save(updatedBooking)).thenReturn(updatedBooking);

        BookingDto result = bookingService.changeBookingStatus(bookingId, isApproved, userId);

        assertEquals(expectedResult, result);
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void changeBookingStatusWhenStatusChangeToRejectedThenSaveStatus() {
        Long bookingId = 1L;
        Long userId = 1L;
        String isApproved = "false";
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(1L);
        item.setName("Test");
        Booking booking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.WAITING, user, item);
        Booking updatedBooking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.REJECTED, user, item);
        BookingDto expectedResult = BookingMapper.toRejectedBookingDto(updatedBooking);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getOwnerId(anyLong())).thenReturn(1L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(booking);
        when(bookingRepository.save(updatedBooking)).thenReturn(updatedBooking);

        BookingDto result = bookingService.changeBookingStatus(bookingId, isApproved, userId);

        assertEquals(expectedResult, result);
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void changeBookingStatusWhenStatusChangedToApprovedThenSaveStatus() {
        Long bookingId = 1L;
        Long userId = 1L;
        String isApproved = "true";
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(1L);
        item.setName("Test");
        Booking booking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.WAITING, user, item);
        Booking updatedBooking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, user, item);
        BookingDto expectedResult = BookingMapper.toBookingDto(updatedBooking);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getOwnerId(anyLong())).thenReturn(1L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(booking);
        when(bookingRepository.save(updatedBooking)).thenReturn(updatedBooking);

        BookingDto result = bookingService.changeBookingStatus(bookingId, isApproved, userId);

        assertEquals(expectedResult, result);
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void changeBookingStatusWhenStatusAlreadyChangedThenThrowWrongStatusException() {
        Long bookingId = 1L;
        Long userId = 1L;
        String isApproved = "false";
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(1L);
        item.setName("Test");
        Booking booking = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, user, item);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getOwnerId(anyLong())).thenReturn(1L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(booking);

        assertThrows(WrongStatusSetException.class,
                () -> bookingService.changeBookingStatus(bookingId, isApproved, userId));

        verify(bookingRepository, never()).save(any());

    }

    @Test
    void deleteBookingWhenUserIsOwnerThenDeleteBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(1L);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        String expectedResult = "Заявка на аренду успешно удалена";
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(booking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.getBookerId(bookingId)).thenReturn(userId);

        String result = bookingService.deleteBooking(bookingId, userId);

        assertEquals(expectedResult, result);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    void deleteBookingWhenUserIsNotOwnerThenThrowOwnerException() {
        Long bookingId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(1L);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(booking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.getBookerId(bookingId)).thenReturn(2L);

        assertThrows(OwnerIdAndUserIdException.class,
                () -> bookingService.deleteBooking(bookingId, userId));

        verify(itemRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).deleteById(bookingId);
    }

    @Test
    void findByIdWhenUserIsOwmerOfItemThenReturnBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        Booking createBooking = new Booking();
        createBooking.setStart(LocalDateTime.of(2023, 3, 10, 17, 45));
        createBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        createBooking.setId(bookingId);
        User user = new User();
        user.setId(userId);
        User user2 = new User();
        user2.setId(2L);
        createBooking.setBooker(user2);
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test");
        item.setOwner(user);
        createBooking.setItem(item);
        createBooking.setStatus(Status.WAITING);
        BookingDto expectedResult = BookingMapper.toBookingDto(createBooking);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(createBooking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.getBookerId(bookingId)).thenReturn(2L);
        when(itemRepository.getOwnerId(itemId)).thenReturn(1L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(createBooking);

        BookingDto result = bookingService.findById(bookingId, userId);

        assertEquals(expectedResult, result);

    }

    @Test
    void findByIdWhenUserIsBookerThenReturnBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        Booking createBooking = new Booking();
        createBooking.setStart(LocalDateTime.of(2023, 3, 10, 17, 45));
        createBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        createBooking.setId(bookingId);
        User user = new User();
        user.setId(2L);
        User user2 = new User();
        user2.setId(userId);
        createBooking.setBooker(user);
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test");
        item.setOwner(user2);
        createBooking.setItem(item);
        createBooking.setStatus(Status.WAITING);
        BookingDto expectedResult = BookingMapper.toBookingDto(createBooking);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(createBooking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.getBookerId(bookingId)).thenReturn(2L);
        when(itemRepository.getOwnerId(itemId)).thenReturn(1L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(createBooking);

        BookingDto result = bookingService.findById(bookingId, userId);

        assertEquals(expectedResult, result);

    }

    @Test
    void findByIdWhenUserIsNotBookerOrOwnerThenOwnerException() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        Booking createBooking = new Booking();
        createBooking.setStart(LocalDateTime.of(2023, 3, 10, 17, 45));
        createBooking.setEnd(LocalDateTime.of(2023, 3, 20, 17, 45));
        createBooking.setId(bookingId);
        User user = new User();
        user.setId(2L);
        User user2 = new User();
        user2.setId(userId);
        createBooking.setBooker(user);
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test");
        item.setOwner(user2);
        createBooking.setItem(item);
        createBooking.setStatus(Status.WAITING);
        BookingDto expectedResult = BookingMapper.toBookingDto(createBooking);
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(createBooking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.getBookerId(bookingId)).thenReturn(3L);
        when(itemRepository.getOwnerId(itemId)).thenReturn(4L);
        when(bookingRepository.getReferenceById(bookingId)).thenReturn(createBooking);

        assertThrows(OwnerIdAndUserIdException.class,
                () -> bookingService.findById(bookingId, userId));

        verify(bookingRepository, times(1)).getReferenceById(bookingId);

    }

    @Test
    void findUserBookingsWhenUserIsBookerOfAllBookingsThenReturnListsOfBookings() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        BookingState stateCurrent = BookingState.CURRENT;
        BookingState statePast = BookingState.PAST;
        BookingState stateFuture = BookingState.FUTURE;
        BookingState stateWaiting = BookingState.WAITING;
        BookingState stateRejected = BookingState.REJECTED;
        BookingState stateAll = BookingState.ALL;
        User userBooker = new User();
        userBooker.setId(1L);
        User userOwner = new User();
        userOwner.setId(2L);
        Item item = new Item();
        item.setOwner(userOwner);
        item.setName("Test");
        item.setId(1L);
        Booking bookingCurrent = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, userBooker, item);
        Booking bookingPast = new Booking(2L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 11, 17, 45),
                Status.APPROVED, userBooker, item);
        Booking bookingFuture = new Booking(3L,
                LocalDateTime.of(2023, 3, 15, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, userBooker, item);
        Booking bookingWaiting = new Booking(4L,
                LocalDateTime.of(2023, 3, 15, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.WAITING, userBooker, item);
        Booking bookingRejected = new Booking(5L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.REJECTED, userBooker, item);
        BookingDto expectedCurrent = BookingMapper.toBookingDto(bookingCurrent);
        BookingDto expectedPast = BookingMapper.toBookingDto(bookingPast);
        BookingDto expectedFuture = BookingMapper.toBookingDto(bookingFuture);
        BookingDto expectedWaiting = BookingMapper.toBookingDto(bookingWaiting);
        BookingDto expectedRejected = BookingMapper.toBookingDto(bookingRejected);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findCurrentUserBookings(userId, pageable))
                .thenReturn(List.of(bookingCurrent));
        when(bookingRepository.findPastUserBookings(userId, pageable))
                .thenReturn(List.of(bookingPast));
        when(bookingRepository.findFutureUserBookings(userId, pageable))
                .thenReturn(List.of(bookingFuture));
        when(bookingRepository.findWaitingUserBookings(userId, pageable))
                .thenReturn(List.of(bookingWaiting));
        when(bookingRepository.findRejectedUserBookings(userId, pageable))
                .thenReturn(List.of(bookingRejected));
        when(bookingRepository.findAllUsersBookings(userId, pageable)).thenReturn(List.of(bookingCurrent,
                bookingPast, bookingFuture, bookingWaiting, bookingRejected));

        List<BookingDto> resultCurrent = bookingService.findUserBookings(userId, stateCurrent, from, size);
        List<BookingDto> resultPast = bookingService.findUserBookings(userId, statePast, from, size);
        List<BookingDto> resultFuture = bookingService.findUserBookings(userId, stateFuture, from, size);
        List<BookingDto> resultWaiting = bookingService.findUserBookings(userId, stateWaiting, from, size);
        List<BookingDto> resultRejected = bookingService.findUserBookings(userId, stateRejected, from, size);
        List<BookingDto> resultAll = bookingService.findUserBookings(userId, stateAll, from, size);

        assertAll(
                () -> assertEquals(List.of(expectedCurrent), resultCurrent),
                () -> assertEquals(List.of(expectedPast), resultPast),
                () -> assertEquals(List.of(expectedFuture), resultFuture),
                () -> assertEquals(List.of(expectedWaiting), resultWaiting),
                () -> assertEquals(List.of(expectedRejected), resultRejected),
                () -> assertEquals(List.of(expectedCurrent, expectedPast, expectedFuture, expectedWaiting,
                        expectedRejected), resultAll)
        );

    }

    @Test
    void findUserBookingsWhenUserIsNotBookerOfBookingsThenReturnEmptyLists() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        BookingState stateCurrent = BookingState.CURRENT;
        BookingState statePast = BookingState.PAST;
        BookingState stateFuture = BookingState.FUTURE;
        BookingState stateWaiting = BookingState.WAITING;
        BookingState stateRejected = BookingState.REJECTED;
        BookingState stateAll = BookingState.ALL;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findCurrentUserBookings(userId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findPastUserBookings(userId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findFutureUserBookings(userId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findWaitingUserBookings(userId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findRejectedUserBookings(userId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllUsersBookings(userId, pageable)).thenReturn(Collections.emptyList());

        List<BookingDto> resultCurrent = bookingService.findUserBookings(userId, stateCurrent, from, size);
        List<BookingDto> resultPast = bookingService.findUserBookings(userId, statePast, from, size);
        List<BookingDto> resultFuture = bookingService.findUserBookings(userId, stateFuture, from, size);
        List<BookingDto> resultWaiting = bookingService.findUserBookings(userId, stateWaiting, from, size);
        List<BookingDto> resultRejected = bookingService.findUserBookings(userId, stateRejected, from, size);
        List<BookingDto> resultAll = bookingService.findUserBookings(userId, stateAll, from, size);

        assertAll(
                () -> assertEquals(Collections.emptyList(), resultCurrent),
                () -> assertEquals(Collections.emptyList(), resultPast),
                () -> assertEquals(Collections.emptyList(), resultFuture),
                () -> assertEquals(Collections.emptyList(), resultWaiting),
                () -> assertEquals(Collections.emptyList(), resultRejected),
                () -> assertEquals(Collections.emptyList(), resultAll)
        );

    }

    @Test
    void findOwnerBookingsWhenUserIsOwnerOfAllBookedItemsThenReturnListsOfBookings() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        BookingState stateCurrent = BookingState.CURRENT;
        BookingState statePast = BookingState.PAST;
        BookingState stateFuture = BookingState.FUTURE;
        BookingState stateWaiting = BookingState.WAITING;
        BookingState stateRejected = BookingState.REJECTED;
        BookingState stateAll = BookingState.ALL;
        User userBooker = new User();
        userBooker.setId(2L);
        User userOwner = new User();
        userOwner.setId(1L);
        Item item = new Item();
        item.setOwner(userOwner);
        item.setName("Test");
        item.setId(1L);
        Item item2 = new Item();
        item2.setOwner(userOwner);
        item2.setName("Test2");
        item2.setId(2L);
        List<Long> itemsId = List.of(1L, 2L);
        Booking bookingCurrent = new Booking(1L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, userBooker, item);
        Booking bookingPast = new Booking(2L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 11, 17, 45),
                Status.APPROVED, userBooker, item);
        Booking bookingFuture = new Booking(3L,
                LocalDateTime.of(2023, 3, 15, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.APPROVED, userBooker, item);
        Booking bookingWaiting = new Booking(4L,
                LocalDateTime.of(2023, 3, 15, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.WAITING, userBooker, item);
        Booking bookingRejected = new Booking(5L,
                LocalDateTime.of(2023, 3, 10, 17, 45),
                LocalDateTime.of(2023, 3, 20, 17, 45),
                Status.REJECTED, userBooker, item);
        BookingDto expectedCurrent = BookingMapper.toBookingDto(bookingCurrent);
        BookingDto expectedPast = BookingMapper.toBookingDto(bookingPast);
        BookingDto expectedFuture = BookingMapper.toBookingDto(bookingFuture);
        BookingDto expectedWaiting = BookingMapper.toBookingDto(bookingWaiting);
        BookingDto expectedRejected = BookingMapper.toBookingDto(bookingRejected);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(1L, 2L));
        when(bookingRepository.findCurrentOwnerBookings(itemsId, pageable))
                .thenReturn(List.of(bookingCurrent));
        when(bookingRepository.findPastOwnerBookings(itemsId, pageable))
                .thenReturn(List.of(bookingPast));
        when(bookingRepository.findFutureOwnerBookings(itemsId, pageable))
                .thenReturn(List.of(bookingFuture));
        when(bookingRepository.findWaitingOwnerBookings(itemsId, pageable))
                .thenReturn(List.of(bookingWaiting));
        when(bookingRepository.findRejectedOwnerBookings(itemsId, pageable))
                .thenReturn(List.of(bookingRejected));
        when(bookingRepository.findAllOwnerBookings(itemsId, pageable)).thenReturn(List.of(bookingCurrent,
                bookingPast, bookingFuture, bookingWaiting, bookingRejected));

        List<BookingDto> resultCurrent = bookingService.findOwnerBookings(userId, stateCurrent, from, size);
        List<BookingDto> resultPast = bookingService.findOwnerBookings(userId, statePast, from, size);
        List<BookingDto> resultFuture = bookingService.findOwnerBookings(userId, stateFuture, from, size);
        List<BookingDto> resultWaiting = bookingService.findOwnerBookings(userId, stateWaiting, from, size);
        List<BookingDto> resultRejected = bookingService.findOwnerBookings(userId, stateRejected, from, size);
        List<BookingDto> resultAll = bookingService.findOwnerBookings(userId, stateAll, from, size);

        assertAll(
                () -> assertEquals(List.of(expectedCurrent), resultCurrent),
                () -> assertEquals(List.of(expectedPast), resultPast),
                () -> assertEquals(List.of(expectedFuture), resultFuture),
                () -> assertEquals(List.of(expectedWaiting), resultWaiting),
                () -> assertEquals(List.of(expectedRejected), resultRejected),
                () -> assertEquals(List.of(expectedCurrent, expectedPast, expectedFuture, expectedWaiting,
                        expectedRejected), resultAll)
        );
    }

    @Test
    void findOwnerBookingsWhenUserIsNotOwnerOfItemsThenReturnEmptyLists() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        BookingState stateCurrent = BookingState.CURRENT;
        BookingState statePast = BookingState.PAST;
        BookingState stateFuture = BookingState.FUTURE;
        BookingState stateWaiting = BookingState.WAITING;
        BookingState stateRejected = BookingState.REJECTED;
        BookingState stateAll = BookingState.ALL;
        List<Long> itemsId = Collections.emptyList();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.getItemsIdsOfOwner(userId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findCurrentOwnerBookings(itemsId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findPastOwnerBookings(itemsId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findFutureOwnerBookings(itemsId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findWaitingOwnerBookings(itemsId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findRejectedOwnerBookings(itemsId, pageable))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllOwnerBookings(itemsId, pageable)).thenReturn(Collections.emptyList());

        List<BookingDto> resultCurrent = bookingService.findOwnerBookings(userId, stateCurrent, from, size);
        List<BookingDto> resultPast = bookingService.findOwnerBookings(userId, statePast, from, size);
        List<BookingDto> resultFuture = bookingService.findOwnerBookings(userId, stateFuture, from, size);
        List<BookingDto> resultWaiting = bookingService.findOwnerBookings(userId, stateWaiting, from, size);
        List<BookingDto> resultRejected = bookingService.findOwnerBookings(userId, stateRejected, from, size);
        List<BookingDto> resultAll = bookingService.findOwnerBookings(userId, stateAll, from, size);

        assertAll(
                () -> assertEquals(Collections.emptyList(), resultCurrent),
                () -> assertEquals(Collections.emptyList(), resultPast),
                () -> assertEquals(Collections.emptyList(), resultFuture),
                () -> assertEquals(Collections.emptyList(), resultWaiting),
                () -> assertEquals(Collections.emptyList(), resultRejected),
                () -> assertEquals(Collections.emptyList(), resultAll)
        );
    }
}