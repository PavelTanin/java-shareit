package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.IncorrectRequestParamsException;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
import ru.practicum.shareit.validator.CustomValidator;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, Long userId) {
        isUserAuthorized(userId);
        CustomValidator.isBookingValid(bookingDto);
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> changeBookingStatus(Long bookingId, String approved, Long userId) {
        isUserAuthorized(userId);
        if (!(approved.equals("true") || approved.equals("false"))) {
            throw new IncorrectRequestParamsException("Некорректно указан статус бронирования");
        }
        return patch("/" + + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> deleteBooking(Long bookingId, Long userId) {
        isUserAuthorized(userId);
        return delete("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findById(Long bookingId, Long userId) {
        isUserAuthorized(userId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findUserBookings(Long userId, BookingState state, Integer from, Integer size) {
        isUserAuthorized(userId);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findOwnerBookings(Long userId, BookingState state, Integer from, Integer size) {
        isUserAuthorized(userId);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    private void isUserAuthorized(Long userId) {
        if (userId == 0) {
            throw new UserNotAuthorizedException("Пользователь не авторизован");
        }
    }
}
