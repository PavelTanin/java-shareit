package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.UserNotAuthorizedException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validator.CustomValidator;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(ItemRequestDto itemRequestDto, Long userId) {
        isUserAuthorized(userId);
        CustomValidator.isRequestValid(itemRequestDto);
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> updateItemRequest(ItemRequestDto itemRequestDto, Long userId, Long requestId) {
        isUserAuthorized(userId);
        CustomValidator.isRequestValid(itemRequestDto);
        return patch("/" + requestId, userId, itemRequestDto);
    }

    public ResponseEntity<Object> deleteItemRequest(Long requestId, Long userId) {
        isUserAuthorized(userId);
        return delete("/" + requestId, userId);
    }

    public ResponseEntity<Object> findRequestById(Long requestId, Long userId) {
        isUserAuthorized(userId);
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAllUserRequests(Long userId) {
        isUserAuthorized(userId);
        return get("", userId);
    }

    public ResponseEntity<Object> findAllRequests(Long userId, Integer from, Integer size) {
        isUserAuthorized(userId);
        return get("/all?from=" + from + "&size=" + size, userId);
    }

    private void isUserAuthorized(Long userId) {
        if (userId == 0) {
            throw new UserNotAuthorizedException("Пользователь не авторизован");
        }
    }



}
