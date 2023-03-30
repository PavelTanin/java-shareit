package ru.practicum.shareit.item.client;

import ru.practicum.shareit.exception.UserNotAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.CustomValidator;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, Long userId) {
        isUserAuthorized(userId);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Long itemId, Long userId) {
        isUserAuthorized(userId);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long itemId, Long userId) {
        isUserAuthorized(userId);
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, Long itemId, Long userId) {
        isUserAuthorized(userId);
        CustomValidator.isCommentValid(commentDto);
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateComment(CommentDto commentDto, Long itemId, Long commentId, Long userId) {
        isUserAuthorized(userId);
        //CustomValidator.isCommentValid(commentDto);
        return patch("/" + itemId + "/comment/" + commentId, userId, commentDto);
    }

    public ResponseEntity<Object> deleteComment(Long commentId, Long itemId, Long userId) {
        isUserAuthorized(userId);
        return delete("/" + itemId + "/comment/" + commentId, userId);
    }

    public ResponseEntity<Object> findItemById(Long userId, Long itemId) {
        isUserAuthorized(userId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findUserAllItems(Long userId, Integer from, Integer size) {
        isUserAuthorized(userId);
        return get("?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> searchItemByNameAndDescription(String text, Integer from, Integer size) {
        return get("/search?text=" + text + "&from=" + from + "&size=" + size);
    }

    private void isUserAuthorized(Long userId) {
        if (userId == 0) {
            throw new UserNotAuthorizedException("Пользователь не авторизован");
        }
    }

}
