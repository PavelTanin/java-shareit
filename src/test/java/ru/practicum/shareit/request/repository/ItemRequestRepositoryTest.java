package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        User user1 = new User(1L, "Test", "test@test.ru");
        User user2 = new User(2L, "Test2", "test2@test.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(user1);
        itemRequest.setDescription("TestDescription");
        itemRequest.setCreated(LocalDateTime.now().withNano(0));
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setRequestor(user2);
        itemRequest2.setDescription("TestDescription2");
        itemRequest2.setCreated(LocalDateTime.now().withNano(0));
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void findAllByRequestorOrderByIdAsc() {
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorOrderByIdAsc(new User(1L, "Test", "test@test.ru"));

        assertEquals(itemRequestList.size(), 1);
        assertEquals(itemRequestList.get(0).getId(), 1L);
    }

    @Test
    void findAllByRequestorIsNotOrderByIdAsc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorIsNotOrderByIdAsc(new User(1L, "Test", "test@test.ru"), pageable);

        assertEquals(itemRequestList.getContent().size(), 1);
        assertEquals(itemRequestList.getContent().get(0).getId(), 2L);
    }

    @Test
    void getRequestorId() {
        Long requestorId = itemRequestRepository.getRequestorId(1L);

        assertEquals(requestorId, 1L);
    }

}