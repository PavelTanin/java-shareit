package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeAll
    void setUp() {
        User user = new User(1L, "Test", "test@test.ru");
        User user2 = new User(2L, "Test2", "test2@test.ru");
        userRepository.saveAll(List.of(user, user2));
        Item item = new Item(1L, "Test", "Test", true,
                user, null);
        Item item2 = new Item(2L, "Test2", "Test2", true,
                user, null);
        itemRepository.saveAll(List.of(item, item2));
        Comment comment = new Comment(1L, user2, item, "TestText", LocalDateTime.now().minusDays(2));
        Comment comment2 = new Comment(2L, user2, item, "TestText2", LocalDateTime.now().minusDays(1));
        Comment comment3 = new Comment(3L, user, item2, "TestText", LocalDateTime.now().withNano(0));
        commentRepository.saveAll(List.of(comment, comment2, comment3));
    }

    @Test
    void getCommentAuthorId() {
        Long authorId = commentRepository.getCommentAuthorId(2L);

        assertEquals(authorId, 2L);
    }

    @Test
    void findAllByItemInOrderByCreatedAsc() {
        List<Comment> commentList = commentRepository
                .findAllByItemInOrderByCreatedAsc(List.of(new Item(2L, "Test2", "Test2", true,
                        new User(1L, "Test", "test@test.ru"), null)));

        assertAll(
                () -> assertEquals(commentList.size(), 1),
                () -> assertEquals(commentList.get(0).getId(), 3L)
        );
    }
}