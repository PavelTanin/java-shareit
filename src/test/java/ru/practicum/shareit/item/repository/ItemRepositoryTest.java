package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeAll
    void setUp() {
        User user = new User(1L, "Test", "test@test.ru");
        User user2 = new User(2L, "Test2", "test2@test.ru");
        userRepository.saveAll(List.of(user, user2));
        ItemRequest itemRequest = new ItemRequest(1L, "TestRequest",
                LocalDateTime.now().withNano(0));
        itemRequestRepository.save(itemRequest);
        Item item = new Item(1L, "Test", "Test", true, user, null);
        Item item2 = new Item(2L, "Test2", "Test2", true, user2, itemRequest);
        Item item3 = new Item(3L, "Test3", "Test3", true, user2, null);
        itemRepository.saveAll(List.of(item, item2, item3));
    }

    @Test
    void findAllByOwner_Id() {
        List<Item> itemList = itemRepository.findAllByOwner_Id(2L, PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(itemList.size(), 2),
                () -> assertEquals(itemList.get(0).getId(), 2L),
                () -> assertEquals(itemList.get(1).getId(), 3L)
        );
    }

    @Test
    void findAllByRequestInOrderByIdAsc() {
        List<Item> itemList = itemRepository
                .findAllByRequestInOrderByIdAsc(List.of(new ItemRequest(1L, "TestRequest",
                        LocalDateTime.now().withNano(0))));

        User user = new User(2L, "Test2", "test2@test.ru");

        assertAll(
                () -> assertEquals(itemList.size(), 1),
                () -> assertEquals(itemList.get(0).getId(), 2L),
                () -> assertEquals(itemList.get(0).getOwner(), user)
        );
    }

    @Test
    void searchByText() {
        List<Item> itemList = itemRepository
                .searchByText("est", PageRequest.of(0, 10));

        assertAll(
                () -> assertEquals(itemList.size(), 3),
                () -> assertEquals(itemList.get(1).getId(), 2L)
        );
    }

    @Test
    void getOwnerId() {
        Long ownerId = itemRepository.getOwnerId(2L);

        assertEquals(ownerId, 2L);
    }

    @Test
    void getItemsIdsOfOwner() {
        List<Long> itemsIdsList = itemRepository.getItemsIdsOfOwner(2L);

        assertAll(
                () -> assertEquals(itemsIdsList.size(), 2),
                () -> assertEquals(itemsIdsList.get(0), 2L),
                () -> assertEquals(itemsIdsList.get(1), 3L)
        );
    }
}