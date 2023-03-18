package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CustomValidator customValidator;

    @Transactional
    public BookingDto createBooking(BookingIncomeInfo bookingIncomeInfo, Long userId) {
        log.info("Попытка создать новую заявку на аренду");
        customValidator.isBookingValid(bookingIncomeInfo);
        isUserAuthorizated(userId);
        User user = userRepository.findById(userId).orElse(null);
        Item item = itemRepository.findById(bookingIncomeInfo.getItemId()).orElse(null);
        if (user == null) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
        if (item == null) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Невозможно арендовать несуществующую вещь");
        }
        if (item.getAvailable() == false) {
            log.info("Предмет {} не доступен для аренды", item.getId());
            throw new ItemNotAvailableException("Данный предмет не доступен для аренды");
        }
        if (item.getOwner().getId().equals(userId)) {
            log.info("Пользователь пытается арендовать собственную вещь");
            throw new BookedByOwnerException("Пользователи не могут бронировать собственные вещи");
        }
        Booking booking = new Booking();
        booking.setStart(bookingIncomeInfo.getStart());
        booking.setEnd(bookingIncomeInfo.getEnd());
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        log.info("Создана заявка на бронирование id: {} от пользователя id: {}", item.getId(), userId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto changeBookingStatus(Long bookingId, String isApproved, Long userId) {
        log.info("Попытка изменить статус заявки на аренду");
        isUserAuthorizated(userId);
        isUserExist(userId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.info("Аренда id: {} не создана", bookingId);
            throw new ObjectNotFoundException("Нет аренды с таким номером");
        }
        var itemId = booking.getItem().getId();
        if (!itemRepository.existsById(itemId)) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Такого предмета не существует");
        }
        if (!itemRepository.getOwnerId(itemId).equals(userId)) {
            log.info("Пользователь не является владельцем данного предмета");
            throw new ObjectNotFoundException("Пользователь не явялется владельцем данного предмета");
        }
        if (isApproved.equals("false") && !booking.getStatus().equals(Status.APPROVED)) {
            log.info("Изменен статус аренды для id: {}", bookingId);
            booking.setStatus(Status.REJECTED);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else if (isApproved.equals("true") && !booking.getStatus().equals(Status.APPROVED)) {
            log.info("Изменен статус аренды для id: {}", bookingId);
            booking.setStatus(Status.APPROVED);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new WrongStatusSetException("Решение по данной аренде уже принято");
        }

    }

    @Transactional
    public String deleteBooking(Long bookingId, Long userId) {
        isUserAuthorizated(userId);
        isUserExist(userId);
        bookingExist(bookingId);
        var itemId = bookingRepository.getReferenceById(bookingId).getItem().getId();
        log.info("Пользователь id: {} пытается удалить заявку на аренду " +
                "id: {} для предмета id: {}", userId, bookingId, itemId);
        isUserAuthorizated(userId);
        if (!bookingRepository.getBookerId(bookingId).equals(userId)) {
            log.info("Пользователь не является автором брони");
            throw new OwnerIdAndUserIdException("Только авторы заявки на аренду могут удалять заявку");
        }
        if (!itemRepository.existsById(itemId)) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Такого предмета не существует");
        }
        log.info("Заявка на аренду id: {} удалена", bookingId);
        bookingRepository.deleteById(bookingId);
        return "Заявка на аренду успешно удалена";
    }

    public BookingDto findById(Long bookingId, Long userId) {
        log.info("Попытка получить информацию об аренде id: {}", bookingId);
        isUserAuthorizated(userId);
        isUserExist(userId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.info("Аренда id: {} не создана", bookingId);
            throw new ObjectNotFoundException("Нет аренды с таким номером");
        }
        var itemId = booking.getItem().getId();
        if (booking.getBooker().getId().equals(userId) || itemRepository.getOwnerId(itemId).equals(userId)) {
            log.info("Получена информация об аренде id: {}", bookingId);
            BookingDto bookingDto = BookingMapper.toBookingDto(booking);
            return bookingDto;
        } else {
            log.info("Пользователь не является владельцем вещи или автором аренды");
            throw new OwnerIdAndUserIdException("Пользователь не является владельцем вещи или автором аренды");
        }
    }

    public List<BookingDto> findUserBookings(Long userId, BookingState state, Integer from, Integer size) {
        log.info("Попытка получить информацию о всех созданных бронях пользователя userId: {}", userId);
        isUserAuthorizated(userId);
        isUserExist(userId);
        customValidator.isPageableParamsCorrect(from, size);
        Pageable pageRequest = PageRequest.of(from, size);
        switch (state) {
            case CURRENT:
                log.info("Получена информация о всех текущих " +
                        "бронированиях пользователя id: {}", userId);
                return bookingRepository.findCurrentUserBookings(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                log.info("Получена информация о всех прошедших " +
                        "бронированиях пользователя id: {}", userId);
                return bookingRepository.findPastUserBookings(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                log.info("Получена информация о всех будущих " +
                        "бронированиях пользователя id: {}", userId);
                return bookingRepository.findFutureUserBookings(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                log.info("Получена информация о всех бронированиях, " +
                        "ожидающих решения, пользователя id: {}", userId);
                return bookingRepository.findWaitingUserBookings(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                log.info("Получена информация о всех отклоненных " +
                        "бронированиях пользователя id: {}", userId);
                return bookingRepository.findRejectedUserBookings(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                log.info("Получена информация о всех бронированиях " +
                        "пользователя id: {}", userId);
                Pageable fixedPageValue = PageRequest.of((from / size), size);
                return bookingRepository.findAllUsersBookings(userId, fixedPageValue)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    public List<BookingDto> findOwnerBookings(Long userId, BookingState state, Integer from, Integer size) {
        log.info("Попытка получить информацию о всех бронях для предметов пользователя userId: {}", userId);
        isUserAuthorizated(userId);
        isUserExist(userId);
        customValidator.isPageableParamsCorrect(from, size);
        Pageable pageRequest = PageRequest.of(from, size);
        List<Long> itemsIds = itemRepository.getItemsIdsOfOwner(userId);
        switch (state) {
            case CURRENT:
                log.info("Получена информация о всех текущих бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.findCurrentOwnerBookings(itemsIds, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                log.info("Получена информация о всех прошедших бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.findPastOwnerBookings(itemsIds, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                log.info("Получена информация о всех будущих бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.findFutureOwnerBookings(itemsIds, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                log.info("Получена информация о всех бронированиях, ожидающих решения," +
                        " для предметов пользователя id: {}", userId);
                return bookingRepository.findWaitingOwnerBookings(itemsIds, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            case REJECTED:
                log.info("Получена информация о всех отклоненных бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.findRejectedOwnerBookings(itemsIds, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());
            default:
                log.info("Получена информация о всех бронированиях для " +
                        "предметов пользователя id: {}", userId);
                return bookingRepository.findAllOwnerBookings(itemsIds, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    private void isUserAuthorizated(Long userId) {
        if (userId <= 0) {
            log.info("Пользователь неавторизован");
            throw new UserNotAuthorizedException("Пользователь неавторизован");
        }
    }

    private void isUserExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
    }

    private void bookingExist(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.info("Аренда id: {} не создана", bookingId);
            throw new ObjectNotFoundException("Нет аренды с таким номером");
        }
    }
}
