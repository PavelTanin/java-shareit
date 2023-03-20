package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.EmptyRequestDescriptionException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.OwnerIdAndUserIdException;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
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
import java.util.Optional;

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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

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

        verify(userRepository, never()).findById(anyLong());
        verify(itemRequestRepository, never()).save(any());

    }

    @Test
    void createRequestWhenUserNotAutorizedThenThrowUserNotAuthorized() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        ItemRequestDto requestDto = new ItemRequestDto();

        assertThrows(UserNotAuthorizedException.class,
                () -> itemRequestService.createRequest(requestDto, userId));

        verify(userRepository, never()).findById(anyLong());
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
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.save(updatedRequest)).thenReturn(updatedRequest);

        ItemRequestDto result = itemRequestService.updateItemRequest(requestDto, userId, requestId);

        assertEquals(expectedResult, result);
    }

    @Test
    void updateItemRequestWhenUserIsNotRequestorThenThrowOwnerException() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = new User();
        user.setId(2L);
        ItemRequest itemRequest = new ItemRequest(requestId, "Test",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        itemRequest.setRequestor(user);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        assertThrows(OwnerIdAndUserIdException.class,
                () -> itemRequestService.updateItemRequest(new ItemRequestDto(), userId, requestId));

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void updateItemRequestWhenRequestHaveIncorrectIdThenThrowIncorrectIdException() {
        Long userId = 1L;
        Long requestId = 0L;
        User user = new User();
        user.setId(userId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.updateItemRequest(new ItemRequestDto(), userId, requestId));

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
                "Test", true, userOwner,
                itemRequest);
        Item requestedItem2 = new Item(2L, "Test",
                "Test", true, userOwner, itemRequest);
        ItemForRequestDto itemDto1 = ItemMapper.toItemForRequestDto(requestedItem1);
        ItemForRequestDto itemDto2 = ItemMapper.toItemForRequestDto(requestedItem2);
        ItemRequestDto expectedResult = RequestMapper.toItemRequestDto(itemRequest);
        expectedResult.setItems(List.of(itemDto1, itemDto2));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestInOrderByIdAsc(anyList()))
                .thenReturn(List.of(requestedItem1, requestedItem2));

        ItemRequestDto result = itemRequestService.findRequestById(requestId, userId);

        assertEquals(expectedResult, result);

    }

    @Test
    void findRequestByIdWhenRequestIsNotExistThenThrowObNFException() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.findRequestById(requestId, userId));

        verify(itemRepository, never()).findAllByRequestInOrderByIdAsc(any());

    }

    @Test
    void findAllUserRequestsWhenUserIsRequestsorThenReturnRequestsList() {
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
        Item requestedItem2 = new Item(2L, "Test2",
                "Test2", true, userOwner, itemRequest);
        Item requestedItem3 = new Item(3L, "Test2",
                "Test2", true, userOwner, itemRequest2);
        ItemForRequestDto requestedItemDto1 = ItemMapper.toItemForRequestDto(requestedItem1);
        ItemForRequestDto requestedItemDto2 = ItemMapper.toItemForRequestDto(requestedItem2);
        ItemForRequestDto requestedItemDto3 = ItemMapper.toItemForRequestDto(requestedItem3);
        ItemRequestDto itemRequestDto1 = RequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto1.setItems(List.of(requestedItemDto1, requestedItemDto2));
        ItemRequestDto itemRequestDto2 = RequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(requestedItemDto3));
        List<ItemRequestDto> expectedResult = List.of(itemRequestDto1, itemRequestDto2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(itemRequestRepository.findAllByRequestorOrderByIdAsc(any()))
                .thenReturn(List.of(itemRequest, itemRequest2));
        when(itemRepository.findAllByRequestInOrderByIdAsc(anyList()))
                .thenReturn(List.of(requestedItem1, requestedItem2, requestedItem3));

        List<ItemRequestDto> result = itemRequestService.findAllUserRequests(userId);

        assertEquals(expectedResult, result);
    }

    @Test
    void findAllRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
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
        List<ItemRequest> requestsToFind = List.of(itemRequest, itemRequest2, itemRequest3);
        Page<ItemRequest> pagedRequests = new PageImpl<>(requestsToFind);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAllByRequestorIsNotOrderByIdAsc(any(), any()))
                .thenReturn(pagedRequests);
        when(itemRepository.findAllByRequestInOrderByIdAsc(anyList()))
                .thenReturn(List.of(requestedItem1, requestedItem2, requestedItem3));

        List<ItemRequestDto> result = itemRequestService.findAllRequests(userId, from, size);

        assertEquals(expectedResult, result);

    }
}