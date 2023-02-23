package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingIncomeInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CustomValidator customValidator;

    @SneakyThrows
    @Transactional
    public BookingDto createBooking(BookingIncomeInfo bookingIncomeInfo, Long userId) {
        var itemId = bookingIncomeInfo.getItemId();
        log.info("Попытка создать новую заявку на аренду");
        userExistAndAuthorizated(userId);
        if (!itemRepository.existsById(itemId)) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Невозможно арендовать несуществующую вещь");
        }
        if (!itemRepository.isAvailable(itemId)) {
            log.info("Предмет {} не доступен для аренды", itemId);
            throw new ItemNotAvailableException("Данный предмет не доступен для аренды");
        }
        if (itemRepository.findById(itemId).get().getOwner().getId() == userId) {
            throw new BookingItemByOwnerException("Пользователи не могут бронироват собственные вещи");
        }
        customValidator.isBookingValid(bookingIncomeInfo);
        Booking booking = new Booking();
        booking.setStart(bookingIncomeInfo.getStart());
        booking.setEnd(bookingIncomeInfo.getEnd());
        booking.setStatus(Status.WAITING);
        booking.setBooker(userRepository.findById(userId).get());
        booking.setItem(itemRepository.findById(itemId).get());
        log.info("Создана заявка на бронирование id: {} от пользователя id: {}", itemId, userId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @SneakyThrows
    @Transactional
    public BookingDto changeBookingStatus(Long bookingId, String isApproved, Long userId) {
        log.info("Попытка изменить статус заявки на аренду");
        bookingExist(bookingId);
        var itemId = bookingRepository.getReferenceById(bookingId).getItem().getId();
        userExistAndAuthorizated(userId);
        if (itemRepository.getReferenceById(itemId).getOwner().getId() != userId) {
            log.info("Пользователь не является владельцем данного предмета");
            throw new ObjectNotFoundException("Пользователь не явялется владельцем данного предмета");
        }
        if (!itemRepository.existsById(itemId)) {
            log.info("Предмет отсутствует");
            throw new ObjectNotFoundException("Такого предмета не существует");
        }
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (isApproved.equals("false") && !booking.getStatus().equals(Status.APPROVED)) {
            booking.setStatus(Status.REJECTED);
            BookingDto bookingDto = BookingMapper.toRejectedBookingDto(bookingRepository.save(booking));
            log.info("Изменен статус аренды для id: {}", bookingId);
            return bookingDto;
        } else if (isApproved.equals("true") && !booking.getStatus().equals(Status.APPROVED)) {
            booking.setStatus(Status.APPROVED);
            BookingDto bookingDto = BookingMapper.toBookingDto(bookingRepository.save(booking));
            log.info("Изменен статус аренды для id: {}", bookingId);
            return bookingDto;
        } else {
            throw new WrongStatusSetException("Решение по данной аренде уже принято");
        }

    }

    @SneakyThrows
    @Transactional
    public String deleteBooking(Long bookingId, Long userId) {
        bookingExist(bookingId);
        var itemId = bookingRepository.getReferenceById(bookingId).getItem().getId();
        log.info("Пользователь id: {} пытается удалить заявку на аренду " +
                "id: {} для предмета id: {}", userId, bookingId, itemId);
        userExistAndAuthorizated(userId);
        if (bookingRepository.getBookerId(bookingId) != userId) {
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

    @SneakyThrows
    public BookingDto findById(Long bookingId, Long userId) {
        log.info("Попытка получить информацию об аренде id: {}", bookingId);
        bookingExist(bookingId);
        var itemId = bookingRepository.getReferenceById(bookingId).getItem().getId();
        userExistAndAuthorizated(userId);
        if (bookingRepository.getBookerId(bookingId) == userId || itemRepository.getOwnerId(itemId) == userId) {
            log.info("Получена информация об аренде id: {}", bookingId);
            BookingDto bookingDto = BookingMapper.toBookingDto(bookingRepository.getReferenceById(bookingId));
            return bookingDto;
        } else {
            log.info("Пользователь не является владельцем вещи или автором аренды");
            throw new OwnerIdAndUserIdException("Пользователь не является владельцем вещи или автором аренды");
        }
    }

    @SneakyThrows
    public List<BookingDto> findUserBookings(Long userId, String state) {
        log.info("Попытка получить информацию о всех созданных бронях пользователя userId: {}", userId);
        userExistAndAuthorizated(userId);
        List<Booking> userBookings = bookingRepository.getBookingsByBookerOrderByStartDesc(userRepository.
                findById(userId).get());
        if (!userBookings.isEmpty()) {
            if (state.equals("ALL")) {
                log.info("Получена информация о всех бронированиях " +
                        "пользователя id: {}", userId);
                return userBookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            } else if (state.equals("CURRENT")) {
                log.info("Получена информация о всех текущих " +
                        "бронированиях пользователя id: {}", userId);
                return userBookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getEnd().isAfter(LocalDateTime.now()))
                        .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            } else if (state.equals("PAST")) {
                log.info("Получена информация о всех прошедших " +
                        "бронированиях пользователя id: {}", userId);
                return userBookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            } else if (state.equals("FUTURE")) {
                log.info("Получена информация о всех будущих " +
                        "бронированиях пользователя id: {}", userId);
                return userBookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            } else if (state.equals("WAITING")) {
                log.info("Получена информация о всех бронированиях, " +
                        "ожидающих решения, пользователя id: {}", userId);
                return userBookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            } else if (state.equals("REJECTED")) {
                log.info("Получена информация о всех отклоненных " +
                        "бронированиях пользователя id: {}", userId);
                return userBookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());
            } else {
                log.info("Получено некоректное значение state - {}", state);
                throw new IncorrectStateParamException("Некорректно указан тип искомых бронирований");
            }
        } else {
            log.info("Нет бронирований созданных пользователем id: {}", userId);
            throw new NoCreatedBookingsException("Пользователь не создавал бронирований");
        }
    }

    @SneakyThrows
    public List<BookingDto> findOwnerBookings(Long userId, String state) {
        log.info("Попытка получить информацию о всех бронях для предметов пользователя userId: {}", userId);
        userExistAndAuthorizated(userId);
        List<Item> userItems = itemRepository.getItemsByOwner(userRepository.findById(userId).get());
        if (!userItems.isEmpty()) {
            if (state.equals("ALL")) {
                log.info("Получена информация о всех бронированиях для " +
                        "предметов пользователя id: {}", userId);
                return bookingRepository.getAllByItemInOrderByStartDesc(userItems).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            } else if (state.equals("CURRENT")) {
                log.info("Получена информация о всех текущих бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.getAllByItemInOrderByStartDesc(userItems).stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getEnd().isAfter(LocalDateTime.now()))
                        .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            } else if (state.equals("PAST")) {
                log.info("Получена информация о всех прошедших бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.getAllByItemInOrderByStartDesc(userItems).stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            } else if (state.equals("FUTURE")) {
                log.info("Получена информация о всех будущих бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.getAllByItemInOrderByStartDesc(userItems).stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            } else if (state.equals("WAITING")) {
                log.info("Получена информация о всех бронированиях, ожидающих решения," +
                        " для предметов пользователя id: {}", userId);
                return bookingRepository.getAllByItemInOrderByStartDesc(userItems).stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            } else if (state.equals("REJECTED")) {
                log.info("Получена информация о всех отклоненных бронированиях " +
                        "для предметов пользователя id: {}", userId);
                return bookingRepository.getAllByItemInOrderByStartDesc(userItems).stream()
                        .map(BookingMapper::toBookingDto)
                        .filter(o -> o.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());
            } else {
                log.info("Получено некоректное значение state - {}", state);
                throw new IncorrectStateParamException("Некорректно указан тип искомых бронирований");
            }
        } else {
            log.info("У пользователя нет созданных предметов");
            throw new NoCreatedItemsException("Пользователь не явлется владельцем какого-либо предмета");
        }
    }

    @SneakyThrows
    private void userExistAndAuthorizated(Long userId) {
        if (userId <= 0) {
            log.info("Пользователь неавторизован");
            throw new UserNotAuthorizedException("Пользователь неавторизован");
        }
        if (!userRepository.existsById(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
    }

    @SneakyThrows
    private void bookingExist(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.info("Аренда id: {} не создана");
            throw new ObjectNotFoundException("Нет аренды с таким номером");
        }
    }
}