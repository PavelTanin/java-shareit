package ru.practicum.shareit.item.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CustomValidator customValidator;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemWhenValidItemWhithoutRequestThenSaveItem() {
        Long userId = 1L;
        ItemDto testItem = new ItemDto("Test", "Test", true);
        Item item = ItemMapper.toItem(testItem);
        when(itemRepository.save(item)).thenReturn(item);
        when(userRepository.existsById(userId)).thenReturn(true);

        ItemDto result = itemService.createItem(testItem, userId);

        verify(customValidator, times(1)).isItemValid(testItem);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(itemRequestRepository, never()).getReferenceById(any());
        assertEquals(testItem, result);

    }

    @Test
    void createItemWhenNotValidItemWhithoutRequestThenThrowItemWrongDescriptionException() {
        Long userId = 1L;
        ItemDto testItem = new ItemDto("Test", "", true);
        Item item = ItemMapper.toItem(testItem);
        doThrow(ItemWrongDescriptionException.class).when(customValidator).isItemValid(testItem);

        assertThrows(ItemWrongDescriptionException.class, () -> itemService.createItem(testItem, userId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void createItemWhenValidItemWhithRequestThenSaveItem() {
        Long userId = 1L;
        Long itemRequestId = 0L;
        ItemDto testItem = new ItemDto("Test", "Test", true);
        ItemRequest itemRequest = new ItemRequest(itemRequestId);
        testItem.setRequestId(0L);
        Item item = ItemMapper.toItem(testItem);
        item.setRequest(itemRequest);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.getReferenceById(itemRequestId)).thenReturn(itemRequest);
        when(itemRepository.save(item)).thenReturn(item);


        ItemDto result = itemService.createItem(testItem, userId);

        verify(customValidator, times(1)).isItemValid(testItem);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).getReferenceById(userId);
        verify(itemRequestRepository, times(1)).getReferenceById(any());
        assertEquals(testItem, result);

    }

    @Test
    void updateItemWhenItemExistThenUpadetItem() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto testItem = new ItemDto("Test", "Test", true);
        ItemDto newItemDto = new ItemDto(itemId, "Renamed", "Renamed", false);
        Item oldItem = ItemMapper.toItem(testItem);
        Item newItem = ItemMapper.toItem(newItemDto);
        oldItem.setId(itemId);
        newItem.setId(itemId);
        when(itemRepository.getOwnerId(itemId)).thenReturn(1L);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getReferenceById(itemId)).thenReturn(oldItem);
        when(itemRepository.save(newItem)).thenReturn(newItem);

        ItemDto result = itemService.updateItem(newItemDto, itemId, userId);

        assertEquals(newItemDto, result);
    }

    @Test
    void updateItemWhenItemNotExistThenThrowObNFException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto testItem = new ItemDto(itemId, "Test", "", true);
        Item item = ItemMapper.toItem(testItem);
        when(userRepository.existsById(userId)).thenReturn(true);
        doThrow(ObjectNotFoundException.class).when(itemRepository).existsById(itemId);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(testItem, itemId, userId));

        verify(itemRepository, never()).save(item);
        verify(itemRepository, never()).getOwnerId(itemId);
        verify(itemRepository, never()).getReferenceById(itemId);
    }

    @Test
    void updateItemWhenUserNotOwnerThenThrowOwnerException() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto testItem = new ItemDto(itemId, "Test", "", true);
        Item item = ItemMapper.toItem(testItem);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getOwnerId(itemId)).thenReturn(2L);

        assertThrows(OwnerIdAndUserIdException.class,
                () -> itemService.updateItem(testItem, itemId, userId));

        verify(itemRepository, never()).save(item);
        verify(itemRepository, never()).getReferenceById(itemId);
    }

    @Test
    void deleteItemWhenItemExistThenDeleteItem() {
        Long userId = 1L;
        Long itemId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.getOwnerId(itemId)).thenReturn(1L);

        String result = itemService.deleteItem(itemId, userId);

        assertEquals("Предмет удален", result);
        verify(itemRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteItemWhenItemNotExistThenThrowObNFException() {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.getOwnerId(itemId)).thenReturn(2L);

        assertThrows(OwnerIdAndUserIdException.class, () -> itemService.deleteItem(itemId, userId));

        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void deleteItemWhenUserNotOwnerThenThrowOwnerEEException() {
        Long userId = 1L;
        Long itemId = 1L;

        assertThrows(ObjectNotFoundException.class, () -> itemService.deleteItem(itemId, userId));

        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void addCommentWhenCommentValidThenSaveComment() {
        Long userId = 1L;
        Long itemId = 1L;
        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .author(new User())
                .item(new Item())
                .text("Test")
                .created(created)
                .build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.bookingsBeforeNowCount(anyLong(), anyLong(), any())).thenReturn(1L);
        when(userRepository.getReferenceById(userId)).thenReturn(new User());
        when(itemRepository.getReferenceById(itemId)).thenReturn(new Item());
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.addComment(commentDto, itemId, userId);

        assertAll(() -> assertEquals(commentDto.getText(), result.getText()),
                () -> assertEquals(commentDto.getAuthorName(), result.getAuthorName()));

    }

    @Test
    void addCommentWhenCommentNotValidThenThrowEmptyCommentException() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        doThrow(EmptyCommentTextException.class).when(customValidator).isCommentValid(commentDto);

        assertThrows(EmptyCommentTextException.class,
                () -> itemService.addComment(commentDto, itemId, userId));

        verify(bookingRepository, never()).bookingsBeforeNowCount(anyLong(), anyLong(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentWhenNoBookingYetThenThrowNoBookingYetException() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);


        assertThrows(NoBookedYetException.class, () -> itemService.addComment(commentDto, itemId, userId));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateCommentWhenNoExceptionsThenUpdateComment() {
        Long userId = 1L;
        Long itemId = 1L;
        Long commentId = 1L;
        LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Comment comment = Comment.builder()
                .author(new User())
                .item(new Item())
                .text("Test")
                .created(created)
                .build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        comment.setText("Test2");
        CommentDto newComment = CommentMapper.toCommentDto(comment);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.getCommentAuthorId(commentId)).thenReturn(1L);
        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto result = itemService.updateComment(newComment, itemId, commentId, userId);

        assertEquals(newComment, result);
    }

    @Test
    void updateCommentWhenUserNotAuthorOftThenThrowOwnerException() {
        Long userId = 1L;
        Long itemId = 1L;
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        doThrow(OwnerIdAndUserIdException.class).when(commentRepository)
                .getCommentAuthorId(commentId);

        assertThrows(OwnerIdAndUserIdException.class,
                () -> itemService.updateComment(commentDto, itemId, commentId, userId));

        verify(commentRepository, never()).getReferenceById(commentId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateCommentWhenCommentNotExistThrowObNFException() {
        Long userId = 1L;
        Long itemId = 1L;
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        doThrow(ObjectNotFoundException.class).when(commentRepository).existsById(commentId);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateComment(commentDto, itemId, commentId, userId));

        verify(commentRepository, never()).getCommentAuthorId(commentId);
        verify(commentRepository, never()).getReferenceById(commentId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteCommentWhenUsersAuthorAndItemExistThenDeleteComment() {
        Long userId = 1L;
        Long itemId = 1L;
        Long commentId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.getCommentAuthorId(commentId)).thenReturn(1L);

        String result = itemService.deleteComment(commentId, itemId, userId);

        assertEquals("Комментарий удален", result);

    }

    @Test
    void findItemByIdWithoutBookingsAndCommentsThenGetIt() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        Item item = ItemMapper.toItem(itemDto);
        itemDto.setId(itemId);
        itemDto.setComments(Collections.emptyList());
        User user = new User();
        user.setId(2L);
        item.setOwner(user);
        item.setId(itemId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(commentRepository.isItemHaveComments(itemId)).thenReturn(0L);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);

        ItemDto result = itemService.findItemById(userId, itemId);

        assertEquals(itemDto, result);

    }

    @Test
    void findItemByIdWithBookingsAndWithCommentsThenGetIt() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        Item item = ItemMapper.toItem(itemDto);
        User user = new User();
        User user2 = new User();
        user.setId(1L);
        user2.setId(2L);
        item.setOwner(user);
        item.setId(itemId);
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test")
                .author(new User())
                .item(item)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        itemDto.setId(itemId);
        itemDto.setComments(List.of(commentDto));
        Booking booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), Status.APPROVED,
                user2, item);
        Booking booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), Status.APPROVED,
                user2, item);
        itemDto.setNextBooking(BookingMapper.toBookingForItemDto(booking1));
        itemDto.setLastBooking(BookingMapper.toBookingForItemDto(booking2));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(commentRepository.isItemHaveComments(itemId)).thenReturn(1L);
        when(commentRepository.getCommentsByItemIdOrderByIdAsc(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.getLastBooking(any(), anyLong())).thenReturn(booking2);
        when(bookingRepository.getNextBooking(any(), anyLong())).thenReturn(booking1);

        ItemDto result = itemService.findItemById(userId, itemId);

        assertEquals(itemDto, result);

    }

    @Test
    void findUserAllItemsWhenListNotEmptyThenReturnItemList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        Pageable pageable = PageRequest.of(from, size);
        ItemDto itemDto = new ItemDto();
        ItemDto itemDto1 = new ItemDto();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRepository.findByOwner(user, pageable)).thenReturn(List.of(new Item(), new Item()));

        List<ItemDto> result = itemService.findUserAllItems(userId, from, size);

        assertEquals(List.of(itemDto, itemDto1), result);

    }

    @Test
    void findUserAllItemsWhenListEmptyThenReturnEmptyList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        Pageable pageable = PageRequest.of(from, size);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRepository.findByOwner(user, pageable)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.findUserAllItems(userId, from, size);

        assertEquals(Collections.emptyList(), result);

    }

    @Test
    void searchItemByNameAndDescriptionWhenTextNotEmptyThenReturnItemList() {
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        String text = "Ручка";
        User user = new User();
        user.setId(1L);
        Item item = new Item(1L, "Ручка шариковая", "Удобно писать",
                true, user, null);
        Item item2 = new Item(2L, "Отвертка", "Удобная ручка", true,
                user, null);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto itemDto1 = ItemMapper.toItemDto(item2);
        when(itemRepository.searchByText(text, pageable)).thenReturn(List.of(item, item2));

        List<ItemDto> result = itemService.searchItemByNameAndDescription(text, from, size);

        assertEquals(List.of(itemDto, itemDto1), result);

    }

    @Test
    void searchItemByNameAndDescriptionWhenTextIsEmptyThenReturnEmptyList() {
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        String text = "";

        List<ItemDto> result = itemService.searchItemByNameAndDescription(text, from, size);

        assertEquals(Collections.emptyList(), result);
        verify(itemRepository, never()).searchByText(anyString(), any());

    }
}