package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Попытка добавить новый предмет");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.getReferenceById(itemDto.getRequestId()));
        }
        log.info("Пользователь id: {} добавил новый предмет id: {}", userId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        log.info("Попытка обновить информацию о предмете id: {}", itemId);
        isUserExist(userId);
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Такого предмета нет");
        }
        if (!itemRepository.getOwnerId(itemId).equals(userId)) {
            log.info("Пользователь пытается обновить информацию о чужом предмете");
            throw new OwnerIdAndUserIdException("Обновлять информацию о предмете могут только владельцы");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Информация о предмете id: {} обновлена", itemId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public String deleteItem(Long itemId, Long userId) {
        log.info("Пользователь id: {} пытается удалить предмет id: {}", itemId);
        isUserExist(userId);
        isItemExist(itemId);
        if (!itemRepository.getOwnerId(itemId).equals(userId)) {
            log.info("Пользователь пытается удалить чужой предмет");
            throw new OwnerIdAndUserIdException("Удалять предметы могут только владельцы");
        }
        itemRepository.deleteById(itemId);
        log.info("Предмет id: {} удален", itemId);
        return "Предмет удален";
    }

    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("Пользователь id: {} добавляет комментарий для предмета id: {}", userId, itemId);
        User user = userRepository.findById(userId).orElse(null);
        Item item = itemRepository.findById(itemId).orElse(null);
        if (user == null) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
        if (item == null) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Такого предмета нет");
        }
        commentDto.setCreated(LocalDateTime.now());
        if (bookingRepository.bookingsBeforeNowCount(itemId, userId, commentDto.getCreated()) == 0L) {
            log.info("Пользователь пока еще не брал в аренду предмет");
            throw new NoBookedYetException("Предмент не арендовался пользователем");
        }
        Comment comment = Comment.builder()
                .author(user)
                .item(item)
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
        log.info("Пользователь id: {} добавил комментарий для предмета id: {}", userId, itemId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto, Long itemId, Long commentId, Long userId) {
        log.info("Пользователь id: {} пытается изменить комментарий id: {} для " +
                "предмета id: {}", userId, commentId, itemId);
        isUserExist(userId);
        isItemExist(itemId);
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            log.info("Комментария id: {} не существует", commentId);
            throw new ObjectNotFoundException("Такого комментария нет");
        }
        if (!commentRepository.getCommentAuthorId(commentId).equals(userId)) {
            log.info("Пользователь id: {} не является автором комментария id: {}", userId, commentId);
            throw new OwnerIdAndUserIdException("Только авторы комментария могут его редактировать");
        }
        /*if (commentDto.getText().isEmpty()) {
            log.info("Получено пустое поле text, комментарий не обновлен");
            throw new EmptyCommentTextException("Невозможно сохранить пустой комментарий");
        }*/
        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public String deleteComment(Long commentId, Long itemId, Long userId) {
        log.info("Пользователь id: {} пытается удалить комментарий id: {} для " +
                "предмета id: {}", userId, commentId, itemId);
        isUserExist(userId);
        isItemExist(itemId);
        isCommentExist(commentId);
        if (!commentRepository.getCommentAuthorId(commentId).equals(userId)) {
            log.info("Пользователь id: {} не является автором комментария id: {}", userId, commentId);
            throw new OwnerIdAndUserIdException("Только авторы комментария могут его редактировать");
        }
        log.info("Комментарий удален");
        commentRepository.deleteById(commentId);
        return "Комментарий удален";
    }

    @Transactional
    public ItemDto findItemById(Long userId, Long itemId) {
        log.info("Попытка получить информацию о предмете id:{}", itemId);
        isUserExist(userId);
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Такого предмета нет");
        }
        List<Item> itemInList = List.of(item);
        List<ItemDto> result = itemInList.stream().map(ItemMapper::toItemDto).collect(toList());
        getCommentsForItem(itemInList, result);
        if (item.getOwner().getId().equals(userId)) {
            log.info("Получена информация о прошлом и следующем бронированиях предмета id: {}", itemId);
            getBookingsForItem(itemInList, result);
        }
        log.info("Получена информация о предмете id: {}", itemId);
        return result.get(0);
    }

    @Transactional
    public List<ItemDto> findUserAllItems(Long userId, Integer from, Integer size) {
        log.info("Попытка получить список предметов пользователя {}", userId);
        isUserExist(userId);
        Pageable pageRequest = PageRequest.of(from, size);
        List<Item> items = itemRepository.findAllByOwner_IdOrderByIdAsc(userId, pageRequest);
        List<ItemDto> result = items.stream().map(ItemMapper::toItemDto).collect(toList());
        getCommentsForItem(items, result);
        getBookingsForItem(items, result);
        log.info("Получен список предметов, размещенных пользователем {}", userId);
        return result;
    }

    private void getCommentsForItem(List<Item> items, List<ItemDto> result) {
        if (!items.isEmpty()) {
            Map<Item, List<Comment>> commentMap = commentRepository
                    .findAllByItemInOrderByCreatedAsc(items)
                    .stream()
                    .collect(groupingBy(Comment::getItem, toList()));
            for (int i = 0; i < result.size(); i++) {
                if (!commentMap.containsKey(items.get(i))) {
                    result.get(i).setComments(Collections.emptyList());
                } else {
                    result.get(i).setComments(commentMap.get(items.get(i)).stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(toList()));
                }
            }
        }
    }


    private void getBookingsForItem(List<Item> items, List<ItemDto> result) {
        if (!items.isEmpty()) {
            Map<Item, List<Booking>> bookingMap = bookingRepository
                    .findAllByItemInAndStatusIsOrderByIdAsc(items,
                            Status.APPROVED).stream()
                    .collect(groupingBy(Booking::getItem, toList()));
            for (int i = 0; i < result.size(); i++) {
                if (bookingMap.containsKey(items.get(i))) {
                    result.get(i).setLastBooking(bookingMap.get(items.get(i)).stream()
                            .sorted(comparing(Booking::getStart).reversed())
                            .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                            .map(BookingMapper::toBookingForItemDto)
                            .findFirst().orElse(null));
                    result.get(i).setNextBooking(bookingMap.get(items.get(i)).stream()
                            .sorted(comparing(Booking::getStart))
                            .filter(o -> o.getStart().isAfter(LocalDateTime.now()))
                            .map(BookingMapper::toBookingForItemDto)
                            .findFirst().orElse(null));
                }
            }
        }
    }

    @Transactional
    public List<ItemDto> searchItemByNameAndDescription(String text, Integer from, Integer size) {
        log.info("Попытка получить список предметов, доступных для аренды, по запросу {}", text);
        if (text == null || text.isBlank()) {
            log.info("В поисковой строке пусто, получен пустой список предметов");
            return Collections.emptyList();
        }
        Pageable pageRequest = PageRequest.of(from, size);
        log.info("Получен список предметов, доступных для аренды, по ключевому слову {}", text);
        return itemRepository.searchByText(text, pageRequest).stream()
                .map(ItemMapper::toItemDto)
                .filter(o -> o.getAvailable() == true)
                .collect(toList());
    }

    private void isUserExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
    }

    private void isItemExist(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Такого предмета нет");
        }
    }

    private void isCommentExist(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            log.info("Комментария id: {} не существует", commentId);
            throw new ObjectNotFoundException("Такого комментария нет");
        }
    }

}
