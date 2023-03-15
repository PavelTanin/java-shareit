package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.OwnerIdAndUserIdException;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
import java.util.List;
import java.util.stream.Collectors;

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
        userExistAndAuthorizated(userId);
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        itemRequest.setRequestor(userRepository.getReferenceById(userId));
        log.info("Пользователь id: {} создал новый запрос {}", userId, itemRequestDto);
        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    public ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long userId, Long requestId) {
        log.info("Попытка изменить существующий запрос id: {}", requestId);
        customValidator.isRequestValid(itemRequestDto);
        userExistAndAuthorizated(userId);
        isRequestExist(requestId);
        if (!itemRequestRepository.getRequestorId(requestId).equals(userId)) {
            log.info("Пользователь пытается изменить чужой запрос");
            throw new OwnerIdAndUserIdException("Только автор запроса может менять его");
        }
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
        itemRequest.setDescription(itemRequestDto.getDescription());
        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    public String deleteItemRequest(Long requestId, Long userId) {
        log.info("Попытка удалить запрос id: {}", requestId);
        userExistAndAuthorizated(userId);
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
        userExistAndAuthorizated(userId);
        isRequestExist(requestId);
        log.info("Получена информация о запросе id: {}", requestId);
        return getItemsForRequest(RequestMapper.toItemRequestDto(itemRequestRepository.getReferenceById(requestId)));
    }

    public List<ItemRequestDto> findAllUserRequests(Long userId) {
        log.info("Пользователь id: {} пытается получить информацию о своих запросах");
        userExistAndAuthorizated(userId);
        User user = userRepository.getReferenceById(userId);
        log.info("Пользователь id: {} получил информацию о своих запросах", userId);
        return itemRequestRepository.findAllByRequestorOrderByIdDesc(user).stream()
                .map(RequestMapper::toItemRequestDto)
                .map(this::getItemsForRequest)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> findAllRequests(Long userId, Integer from, Integer size) {
        log.info("Попытка получить {} станицу из {} записей о запросах от пользователя id: {}", from, size, userId);
        userExistAndAuthorizated(userId);
        customValidator.isPageableParamsCorrect(from, size);
        Pageable pageRequest = PageRequest.of(from, size);
        log.info("Получен список всех запросов");
        return itemRequestRepository.findAllRequests(userId, pageRequest).stream()
                .map(RequestMapper::toItemRequestDto)
                .map(this::getItemsForRequest)
                .collect(Collectors.toList());
    }

    private ItemRequestDto getItemsForRequest(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.getRequestItems(itemRequestDto.getId()).stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.toList()));
        return itemRequestDto;
    }

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

    private void isRequestExist(Long requestId) {
        if (requestId <= 0) {
            log.info("Получен некорректный идентификатор запроса id: {}", requestId);
            throw new IncorrectIdException("Получен некорректный id запроса");
        }
        if (!itemRequestRepository.existsById(requestId)) {
            log.info("Попытка обновить несуществующий запрос");
            throw new ObjectNotFoundException("Запрос не найден");
        }
    }
}
