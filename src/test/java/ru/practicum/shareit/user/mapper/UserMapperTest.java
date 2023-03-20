package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserIdDto;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomMappersTest {

    @Test
    void toUserDtoAndToUserMapperTest() {
        User user = new User(1L, "Test", "test2@test.ru");
        UserDto userDto = new UserDto(1L, "test2@test.ru", "Test");

        assertAll(
                () -> assertEquals(UserMapper.toUserDto(user), userDto),
                () -> assertEquals(UserMapper.toUser(userDto), user)
        );
    }

    @Test
    void toCommentDtoTest() {
        User user = new User(1L, "Test", "test@test.ru");
        User userAuthor = new User(2L, "Test2", "test2@test.ru");
        Item item = new Item(1L, "Test", "Test", true, user, null);
        LocalDateTime createTime = LocalDateTime.now();
        Comment comment = new Comment(1L, userAuthor, item, "TestText",
                createTime);
        CommentDto commentDto = new CommentDto(1L, "TestText", "Test2", createTime);

        assertEquals(CommentMapper.toCommentDto(comment), commentDto);

    }

    @Test
    void toRequestDtoAndToRequestTest() {
        LocalDateTime createTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "TestText", createTime);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "TestText", createTime);

        assertAll(
                () -> assertEquals(RequestMapper.toItemRequestDto(itemRequest), itemRequestDto),
                () -> assertEquals(RequestMapper.toItemRequest(itemRequestDto), itemRequest)
        );
    }

    @Test
    void toItemDtoWithRequestTest() {
        LocalDateTime createTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "TestText", createTime);
        Item item = new Item("Test", "Test", true);
        item.setId(1L);
        item.setRequest(itemRequest);
        ItemDto itemDto = new ItemDto(1L, "Test", "Test", true);
        itemDto.setRequestId(1L);

        assertEquals(ItemMapper.toItemDto(item), itemDto);
    }

    @Test
    void toItemDtoWithoutRequestTest() {
        Item item = new Item("Test", "Test", true);
        item.setId(1L);
        ItemDto itemDto = new ItemDto(1L, "Test", "Test", true);

        assertEquals(ItemMapper.toItemDto(item), itemDto);
    }

    @Test
    void toItemForRequestDtoWithRequestTest() {
        LocalDateTime createTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "TestText", createTime);
        User owner = new User(1L, "Test", "test@test.ru");
        Item item = new Item(1L, "Test", "Test", true,
                owner, itemRequest);
        ItemForRequestDto itemForRequestDto = new ItemForRequestDto(1L, "Test", "Test", true,
                itemRequest.getId(), owner.getId());

        assertEquals(ItemMapper.toItemForRequestDto(item), itemForRequestDto);
    }

    @Test
    void toItemForRequestDtoWithoutRequestTest() {
        User owner = new User(1L, "Test", "test@test.ru");
        Item item = new Item(1L, "Test", "Test", true,
                owner, null);
        ItemForRequestDto itemForRequestDto = new ItemForRequestDto(1L, "Test", "Test", true,
                null, owner.getId());

        assertEquals(ItemMapper.toItemForRequestDto(item), itemForRequestDto);
    }

    @Test
    void toBookingDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(3).withNano(0);
        User owner = new User(1L, "Test", "test@test.ru");
        Item item = new Item(1L, "Test", "Test", true,
                owner, null);
        User booker = new User(2L, "Test2", "test2@test.ru");
        UserIdDto bookerIdDto = new UserIdDto(2L);
        ItemForBookingDto itemForBookingDto = new ItemForBookingDto(1L, "Test");
        Booking booking = new Booking(1L, start, end, Status.APPROVED, booker, item);
        BookingDto bookingDto = new BookingDto(1L, start,
                end, Status.APPROVED, bookerIdDto, itemForBookingDto);

        assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void toBookingForItemDtoWhenBookingIsNullTest() {
        Booking booking = null;

        assertEquals(BookingMapper.toBookingForItemDto(booking), null);
    }

    @Test
    void toBookingForItemDtoWhenBookingIsNotNullTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(3).withNano(0);
        User owner = new User(1L, "Test", "test@test.ru");
        Item item = new Item(1L, "Test", "Test", true,
                owner, null);
        User booker = new User(2L, "Test2", "test2@test.ru");
        Booking booking = new Booking(1L, start, end, Status.APPROVED, booker, item);
        BookingForItemDto bookingForItemDto = new BookingForItemDto(1L, booker.getId(), start, end);

        assertEquals(BookingMapper.toBookingForItemDto(booking), bookingForItemDto);
    }
}