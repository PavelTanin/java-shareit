package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.OwnerIdAndUserIdException;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final CustomValidator customValidator;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Попытка добавить новый запрос");
        customValidator.isRequestValid(itemRequestDto);
        isUserAuthorizated(userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        itemRequest.setRequestor(user);
        log.info("Пользователь id: {} создал новый запрос {}", userId, itemRequestDto);
        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    public ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long userId, Long requestId) {
        log.info("Попытка изменить существующий запрос id: {}", requestId);
        customValidator.isRequestValid(itemRequestDto);
        isUserAuthorizated(userId);
        isUserExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElse(null);
        if (itemRequest == null) {
            log.info("Попытка обновить несуществующий запрос");
            throw new ObjectNotFoundException("Запрос не найден");
        }
        if (!itemRequest.getRequestor().getId().equals(userId)) {
            log.info("Пользователь пытается изменить чужой запрос");
            throw new OwnerIdAndUserIdException("Только автор запроса может менять его");
        }

        itemRequest.setDescription(itemRequestDto.getDescription());
        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    public String deleteItemRequest(Long requestId, Long userId) {
        log.info("Попытка удалить запрос id: {}", requestId);
        isUserAuthorizated(userId);
        isUserExist(userId);
        isRequestExist(requestId);
        if (!itemRequestRepository.getRequestorId(requestId).equals(userId)) {
            log.info("Пользователь пытается удалить чужой запрос");
            throw new OwnerIdAndUserIdException("Только автор запроса может удалить его");
        }
        log.info("Пользователь id: {} удалил запрос id: {}", userId, requestId);
        itemRequestRepository.deleteById(requestId);
        return "Запрос успешно удален";
    }


    public ItemRequestDto findRequestById(Long requestId, Long userId) {
        log.info("Пользователь id: {} пытаетеся получить информацию о запросе id: {}", userId, requestId);
        isUserAuthorizated(userId);
        isUserExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElse(null);
        if (itemRequest == null) {
            log.info("Запроса с id: {} не существует", requestId);
            throw new ObjectNotFoundException("Запрос не найден");
        }
        List<ItemRequest> requestInList = List.of(itemRequest);
        List<ItemRequestDto> result = requestInList.stream().map(RequestMapper::toItemRequestDto).collect(toList());
        getItemsForRequests(requestInList, result);
        log.info("Получена информация о запросе id: {}", requestId);
        return result.get(0);
    }

    public List<ItemRequestDto> findAllUserRequests(Long userId) {
        log.info("Пользователь id: {} пытается получить информацию о своих запросах");
        isUserAuthorizated(userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorOrderByIdAsc(user);
        List<ItemRequestDto> result = requests.stream().map(RequestMapper::toItemRequestDto).collect(toList());
        getItemsForRequests(requests, result);
        log.info("Пользователь id: {} получил информацию о своих запросах", userId);
        return result;
    }

    public List<ItemRequestDto> findAllRequests(Long userId, Integer from, Integer size) {
        log.info("Попытка получить {} станицу из {} записей о запросах от пользователя id: {}", from, size, userId);
        isUserAuthorizated(userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
        customValidator.isPageableParamsCorrect(from, size);
        Pageable pageRequest = PageRequest.of(from, size);
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIsNotOrderByIdAsc(user, pageRequest).getContent();
        List<ItemRequestDto> result = requests.stream().map(RequestMapper::toItemRequestDto).collect(toList());
        getItemsForRequests(requests, result);
        log.info("Получен список всех запросов");
        return result;
    }

    private void getItemsForRequests(List<ItemRequest> requests, List<ItemRequestDto> result) {
        if (!requests.isEmpty()) {
            Map<ItemRequest, List<Item>> itemsByRequests = itemRepository
                    .findAllByRequestInOrderByIdAsc(requests)
                    .stream()
                    .collect(groupingBy(Item::getRequest, toList()));
            for (int i = 0; i < requests.size(); i++) {
                if (!itemsByRequests.containsKey(requests.get(i))) {
                    result.get(i).setItems(Collections.emptyList());
                } else {
                    result.get(i).setItems(itemsByRequests.get(requests.get(i)).stream()
                            .map(ItemMapper::toItemForRequestDto)
                            .collect(toList()));
                }
            }
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

    private void isRequestExist(Long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            log.info("Попытка обновить несуществующий запрос");
            throw new ObjectNotFoundException("Запрос не найден");
        }
    }
}
