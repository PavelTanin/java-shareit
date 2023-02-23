package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.CustomValidator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final CustomValidator customValidator;

    @SneakyThrows
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Попытка добавить новый предмет");
        isUserExistAndAuthorizated(userId);
        customValidator.isItemValid(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getReferenceById(userId));
        log.info("Пользователь id: {} добавил новый предмет id: {}", userId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @SneakyThrows
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        log.info("Попытка обновить информацию о предмете id: {}", itemId);
        isUserExistAndAuthorizated(userId);
        isItemExist(itemId);
        if (itemRepository.getReferenceById(itemId).getOwner().getId() != userId) {
            log.info("Пользователь пытается обновить информацию о чужом предмете");
            throw new OwnerIdAndUserIdException("Обновлять информацию о предмете могут только владельцы");
        }
        Item item = itemRepository.findById(itemId).get();
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
        isUserExistAndAuthorizated(userId);
        isItemExist(itemId);
        itemRepository.deleteById(itemId);
        log.info("Предмет id: {} удален", itemId);
        return "Предмет удален";
    }

    @SneakyThrows
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("Пользователь id: {} добавляет комментарий для предмета id: {}", userId, itemId);
        isUserExistAndAuthorizated(userId);
        isItemExist(itemId);
        LocalDateTime createTime = LocalDateTime.now();
        if (commentDto.getText().isEmpty() || commentDto.getText().equals(null)) {
            log.info("Комментарий не содержит текст");
            throw new EmptyCommentTextException("Текст комментария не может быть пустым");
        }
        if (bookingRepository.bookingsBeforeNowCount(itemId, userId, createTime) == 0) {
            log.info("Пользователь пока еще не брал в аренду предмет");
            throw new NoBookedYetException("Предмент не арендовался пользователем");
        }
        Comment comment = Comment.builder()
                .author(userRepository.getReferenceById(userId))
                .item(itemRepository.getReferenceById(itemId))
                .text(commentDto.getText())
                .created(createTime)
                .build();
        log.info("Пользователь id: {} добавил комментарий для предмета id: {}", userId, itemId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @SneakyThrows
    public CommentDto updateComment(CommentDto commentDto, Long itemId, Long commentId, Long userId) {
        log.info("Пользователь id: {} пытается изменить комментарий id: {} для " +
                "предмета id: {}", userId, commentId, itemId);
        isUserExistAndAuthorizated(userId);
        isItemExist(itemId);
        isCommentExist(commentId);
        if (commentRepository.getReferenceById(commentId).getAuthor().getId() != userId) {
            log.info("Пользователь id: {} не является автором комментария id: {}", userId, commentId);
            throw new OwnerIdAndUserIdException("Только авторы комментария могут его редактировать");
        }
        if (commentDto.getText().isEmpty()) {
            log.info("Получен пустое поле text, комментарий не обновлен");
            throw new EmptyCommentTextException("Невозможно сохранить пустой комментарий");
        }
        Comment comment = commentRepository.getReferenceById(commentId);
        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @SneakyThrows
    public String deleteComment(Long commentId, Long itemId, Long userId) {
        log.info("Пользователь id: {} пытается удалить комментарий id: {} для " +
                "предмета id: {}", userId, commentId, itemId);
        isUserExistAndAuthorizated(userId);
        isItemExist(itemId);
        isCommentExist(commentId);
        log.info("Комментарий удален");
        commentRepository.deleteById(commentId);
        return "Комментарий удален";
    }

    @SneakyThrows
    @Transactional
    public ItemDto findItemById(Long userId, Long itemId) {
        log.info("Попытка получить информацию о предмете id:{}", itemId);
        isUserExistAndAuthorizated(userId);
        isItemExist(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.getReferenceById(itemId));
        if (commentRepository.isItemHaveComments(itemId) > 0) {
            log.info("Получены все комментарии для предмета id: {}", itemId);
            itemDto.setComments(getItemComments(itemId));
        }
        itemDto.setComments(getItemComments(itemId));
        if (item.getOwner().getId() == userId) {
            log.info("Получена информация о прошлом и следующем бронированиях предмета id: {}", itemId);
            return getItemWithBookings(itemDto);
        }
        log.info("Получена информация о предмете id: {}", itemId);
        return itemDto;
    }

    @SneakyThrows
    @Transactional
    public List<ItemDto> findUserAllItems(Long userId) {
        log.info("Попытка получить список предметов пользователя {}", userId);
        log.info("Получен список предметов, размещенных пользователем {}", userId);
        return itemRepository.findByOwner(userRepository.getReferenceById(userId)).stream()
                .map(ItemMapper::toItemDto)
                .map(this::getItemWithBookings)
                .collect(Collectors.toList());
    }

    private List<CommentDto> getItemComments(Long itemId) {
        return commentRepository.getCommentsByItem_IdOrderByIdAsc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private ItemDto getItemWithBookings(ItemDto itemDto) {
        LocalDateTime nowTime = LocalDateTime.now();
        itemDto.setLastBooking(BookingMapper
                .toBookingForItemDto(Optional.ofNullable(bookingRepository.getLastBooking(nowTime, itemDto.getId()))
                        .orElse(null)));
        itemDto.setNextBooking(BookingMapper
                .toBookingForItemDto(Optional.ofNullable(bookingRepository.getNextBooking(nowTime, itemDto.getId()))
                        .orElse(null)));
        return itemDto;
    }

    @SneakyThrows
    @Transactional
    public List<ItemDto> searchItemByNameAndDescription(String text) {
        log.info("Попытка получить список предметов, доступных для аренды, по запросу {}", text);
        if (text == null || text.isBlank()) {
            log.info("В поисковой строке пусто, получен пустой список предметов");
            return Collections.emptyList();
        }
        log.info("Получен список предметов, доступных для аренды, по ключевому слову {}", text);
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .filter(o -> o.getAvailable() == true)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private void isUserExistAndAuthorizated(Long userId) {
        if (userId <= 0) {
            log.info("Пользователь не авторизован");
            throw new UserNotAuthorizedException("Пользователь не авторизован");
        }
        if (!userRepository.existsById(userId)) {
            log.info("Пользователь не зарегестрирован");
            throw new ObjectNotFoundException("Пользователь не зарегестрирован");
        }
    }

    @SneakyThrows
    private void isItemExist(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.info("Предмет с id:{} не найден", itemId);
            throw new ObjectNotFoundException("Такого предмета нет");
        }
    }

    @SneakyThrows
    private void isCommentExist(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            log.info("Комментария id: {} не существует", commentId);
            throw new ObjectNotFoundException("Такого комментария нет");
        }
    }

}
