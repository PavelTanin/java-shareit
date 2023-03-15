package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EmptyRequestDescriptionException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.OwnerIdAndUserIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
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
class ItemRequestServiceImplTest {

    @Mock
    private CustomValidator customValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequestWhenRequestIsValidThenSaveAndReturnRequest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest(null, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        itemRequest.setRequestor(user);
        ItemRequestDto expectedResult = RequestMapper.toItemRequestDto(itemRequest);
        expectedResult.setItems(Collections.emptyList());
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createRequest(requestDto, userId);

        assertEquals(expectedResult, result);

    }

    @Test
    void createRequestWhenRequestIsNotValidThenSaveAndReturnRequest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequestDto requestDto = new ItemRequestDto();
        doThrow(EmptyRequestDescriptionException.class).when(customValidator).isRequestValid(requestDto);

        assertThrows(EmptyRequestDescriptionException.class,
                () -> itemRequestService.createRequest(requestDto, userId));

        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).getReferenceById(anyLong());
        verify(itemRequestRepository, never()).save(any());

    }

    @Test
    void updateItemRequestWhenUserIsRequestorThenUpdateAndReturnRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest(requestId, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest.setRequestor(user);
        ItemRequest updatedRequest = new ItemRequest(requestId, "Renamed",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        updatedRequest.setRequestor(user);
        ItemRequestDto expectedResult = RequestMapper.toItemRequestDto(updatedRequest);
        expectedResult.setItems(Collections.emptyList());
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Renamed");
        when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.getRequestorId(requestId)).thenReturn(1L);
        when(itemRequestRepository.getReferenceById(requestId)).thenReturn(itemRequest);
        when(itemRequestRepository.save(updatedRequest)).thenReturn(updatedRequest);

        ItemRequestDto result = itemRequestService.updateItemRequest(requestDto, userId, requestId);

        assertEquals(expectedResult, result);
    }

    @Test
    void updateItemRequestWhenUserIsNotRequestorThenThrowOwnerException() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User();
        user.setId(userId);
        when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.getRequestorId(requestId)).thenReturn(2L);

        assertThrows(OwnerIdAndUserIdException.class,
                () -> itemRequestService.updateItemRequest(new ItemRequestDto(), userId, requestId));

        verify(itemRequestRepository, never()).getReferenceById(anyLong());
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void deleteItemRequestWhenUserIsRequestorThenDeleteRequestAndReturnMessage() {
        Long userId = 1L;
        Long requestId = 1L;
        String expectedResult = "Запрос успешно удален";
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        when(itemRequestRepository.getRequestorId(requestId)).thenReturn(1L);

        String result = itemRequestService.deleteItemRequest(requestId, userId);

        assertEquals(expectedResult, result);
        verify(itemRequestRepository, times(1)).deleteById(requestId);
    }

    @Test
    void deleteItemRequestWhenUserIsNotRequestorThenThrowOwnerException() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        when(itemRequestRepository.getRequestorId(requestId)).thenReturn(2L);

        assertThrows(OwnerIdAndUserIdException.class,
                () -> itemRequestService.deleteItemRequest(requestId, userId));

        verify(itemRequestRepository, never()).deleteById(requestId);
    }

    @Test
    void findRequestByIdWhenRequestIsExistThenReturnItemRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        User userRequestor = new User();
        userRequestor.setId(userId);
        User userOwner = new User();
        userOwner.setId(2L);
        ItemRequest itemRequest = new ItemRequest(1L, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest.setRequestor(userRequestor);
        Item requestedItem1 = new Item(1L, "Test",
                "Test", true, userOwner, itemRequest);
        Item requestedItem2 = new Item(2L, "Test",
                "Test", true, userOwner, itemRequest);
        ItemForRequestDto itemDto1 = ItemMapper.toItemForRequestDto(requestedItem1);
        ItemForRequestDto itemDto2 = ItemMapper.toItemForRequestDto(requestedItem2);
        ItemRequestDto expectedResult = RequestMapper.toItemRequestDto(itemRequest);
        expectedResult.setItems(List.of(itemDto1, itemDto2));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(requestId)).thenReturn(true);
        when(itemRequestRepository.getReferenceById(requestId)).thenReturn(itemRequest);
        when(itemRepository.getRequestItems(anyLong()))
                .thenReturn(List.of(requestedItem1, requestedItem2));

        ItemRequestDto result = itemRequestService.findRequestById(requestId, userId);

        assertEquals(expectedResult, result);

    }

    @Test
    void findRequestByIdWhenRequestIsNotExistThenThrowObNFException() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(requestId)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.findRequestById(requestId, userId));

        verify(itemRequestRepository, never()).getReferenceById(requestId);
        verify(itemRepository, never()).getRequestItems(anyLong());

    }

    @Test
    void findAllUserRequestsWhenUserIsOwnerOfRequestsThenReturnRequestsList() {
        Long userId = 1L;
        User userRequestor = new User();
        userRequestor.setId(1L);
        User userOwner = new User();
        userOwner.setId(2L);
        ItemRequest itemRequest = new ItemRequest(1L, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest.setRequestor(userRequestor);
        ItemRequest itemRequest2 = new ItemRequest(2L, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest2.setRequestor(userRequestor);
        Item requestedItem1 = new Item(1L, "Test",
                "Test", true, userOwner, itemRequest);
        Item requestedItem2 = new Item(2L, "Test",
                "Test", true, userOwner, itemRequest);
        Item requestedItem3 = new Item(3L, "Test",
                "Test", true, userOwner, itemRequest2);
        ItemForRequestDto requestedItemDto1 = ItemMapper.toItemForRequestDto(requestedItem1);
        ItemForRequestDto requestedItemDto2 = ItemMapper.toItemForRequestDto(requestedItem2);
        ItemForRequestDto requestedItemDto3 = ItemMapper.toItemForRequestDto(requestedItem3);
        ItemRequestDto itemRequestDto1 = RequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto1.setItems(List.of(requestedItemDto1, requestedItemDto2));
        ItemRequestDto itemRequestDto2 = RequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(requestedItemDto3));
        List<ItemRequestDto> expectedResult = List.of(itemRequestDto1, itemRequestDto2);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(userRequestor);
        when(itemRequestRepository.findAllByRequestorOrderByIdDesc(userRequestor))
                .thenReturn(List.of(itemRequest, itemRequest2));
        when(itemRepository.getRequestItems(anyLong()))
                .thenReturn(List.of(requestedItem1, requestedItem2), List.of(requestedItem3));

        List<ItemRequestDto> result = itemRequestService.findAllUserRequests(userId);

        assertEquals(expectedResult, result);
    }

    @Test
    void findAllUserRequestsWhenUserIsNotOwnerOfRequestsThenReturnEmptyList() {
        Long userId = 1L;
        User userRequestor = new User();
        userRequestor.setId(2L);
        User userOwner = new User();
        userOwner.setId(1L);
        List<ItemRequestDto> expectedResult = Collections.emptyList();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(userRequestor);
        when(itemRequestRepository.findAllByRequestorOrderByIdDesc(userRequestor))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.findAllUserRequests(userId);

        assertEquals(expectedResult, result);
    }

    @Test
    void findAllRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);
        User userRequestor = new User();
        userRequestor.setId(1L);
        User userOwner = new User();
        userOwner.setId(2L);
        ItemRequest itemRequest = new ItemRequest(1L, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest.setRequestor(userRequestor);
        ItemRequest itemRequest2 = new ItemRequest(2L, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest2.setRequestor(userRequestor);
        ItemRequest itemRequest3 = new ItemRequest(3L, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest2.setRequestor(userRequestor);
        Item requestedItem1 = new Item(1L, "Test",
                "Test", true, userOwner, itemRequest);
        Item requestedItem2 = new Item(2L, "Test",
                "Test", true, userOwner, itemRequest);
        Item requestedItem3 = new Item(3L, "Test",
                "Test", true, userOwner, itemRequest2);
        ItemForRequestDto requestedItemDto1 = ItemMapper.toItemForRequestDto(requestedItem1);
        ItemForRequestDto requestedItemDto2 = ItemMapper.toItemForRequestDto(requestedItem2);
        ItemForRequestDto requestedItemDto3 = ItemMapper.toItemForRequestDto(requestedItem3);
        ItemRequestDto itemRequestDto1 = RequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto1.setItems(List.of(requestedItemDto1, requestedItemDto2));
        ItemRequestDto itemRequestDto2 = RequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(requestedItemDto3));
        ItemRequestDto itemRequestDto3 = RequestMapper.toItemRequestDto(itemRequest3);
        itemRequestDto3.setItems(Collections.emptyList());
        List<ItemRequestDto> expectedResult = List.of(itemRequestDto1, itemRequestDto2, itemRequestDto3);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllRequests(userId, pageable))
                .thenReturn(List.of(itemRequest, itemRequest2, itemRequest3));
        when(itemRepository.getRequestItems(anyLong())).thenReturn(List.of(requestedItem1, requestedItem2),
                List.of(requestedItem3), Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.findAllRequests(userId, from, size);

        assertEquals(expectedResult, result);

    }
}