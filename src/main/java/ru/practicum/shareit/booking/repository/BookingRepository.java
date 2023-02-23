package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select booker_id from booking where id = ?1", nativeQuery = true)
    Long getBookerId(Long bookingId);

    List<Booking> getBookingsByBookerOrderByStartDesc(User user);

    List<Booking> getAllByItemInOrderByStartDesc(List<Item> items);

    @Query(value = "SELECT * FROM BOOKING WHERE START_TIME >= ?1 " +
            "AND ITEM_ID = ?2 ORDER BY START_TIME LIMIT 1", nativeQuery = true)
    Booking getNextBooking(LocalDateTime nowTime, Long itemId);

    @Query(value = "SELECT * FROM BOOKING WHERE START_TIME < ?1 " +
            "AND ITEM_ID = ?2 ORDER BY START_TIME LIMIT 1", nativeQuery = true)
    Booking getLastBooking(LocalDateTime nowTime, Long itemId);

    @Query(value = "SELECT count(*) FROM BOOKING WHERE ITEM_ID = ?1 " +
            "AND BOOKER_ID = ?2 AND START_TIME <= ?3", nativeQuery = true)
    Long bookingsBeforeNowCount(Long itemId, Long userId, LocalDateTime nowTime);


}
