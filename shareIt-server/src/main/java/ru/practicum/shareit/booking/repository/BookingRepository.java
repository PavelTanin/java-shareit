package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select booker_id from booking where id = ?1", nativeQuery = true)
    Long getBookerId(Long bookingId);

    @Query(value = "SELECT * FROM Booking where booker_id = ?1 ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findAllUsersBookings(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM Booking WHERE booker_id = ?1 " +
            "AND (now() BETWEEN START_TIME AND END_TIME) ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findCurrentUserBookings(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM Booking WHERE BOOKER_ID = ?1 " +
            "AND END_TIME <= now() ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findPastUserBookings(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE BOOKER_ID = ?1 " +
            "AND START_TIME >= now() ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findFutureUserBookings(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE BOOKER_ID = ?1 " +
            "AND STATUS = 'WAITING' ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findWaitingUserBookings(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE BOOKER_ID = ?1 " +
            "AND STATUS = 'REJECTED' ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findRejectedUserBookings(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE ITEM_ID IN ?1 ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findAllOwnerBookings(List<Long> itemsIds, Pageable pageable);

    @Query(value = "SELECT * FROM Booking WHERE ITEM_ID IN ?1 " +
            "AND (now() BETWEEN START_TIME AND END_TIME) ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findCurrentOwnerBookings(List<Long> itemsIds, Pageable pageable);

    @Query(value = "SELECT * FROM Booking WHERE ITEM_ID IN ?1 " +
            "AND END_TIME <= now() ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findPastOwnerBookings(List<Long> itemsIds, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE ITEM_ID IN ?1 " +
            "AND START_TIME >= now() ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findFutureOwnerBookings(List<Long> itemsIds, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE ITEM_ID IN ?1 " +
            "AND STATUS = 'WAITING' ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findWaitingOwnerBookings(List<Long> itemsIds, Pageable pageable);

    @Query(value = "SELECT * FROM BOOKING WHERE ITEM_ID IN ?1 " +
            "AND STATUS = 'REJECTED' ORDER BY START_TIME DESC", nativeQuery = true)
    List<Booking> findRejectedOwnerBookings(List<Long> itemsIds, Pageable pageable);

    @Query(value = "SELECT count(*) FROM BOOKING WHERE ITEM_ID = ?1 " +
            "AND BOOKER_ID = ?2 AND END_TIME <= ?3", nativeQuery = true)
    Long bookingsBeforeNowCount(Long itemId, Long userId, LocalDateTime nowTime);

    List<Booking> findAllByItemInAndStatusIsOrderByIdAsc(List<Item> items, Status status);


}
