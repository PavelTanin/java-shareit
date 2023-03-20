package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query(value = "select author_id from comments where id = ?1", nativeQuery = true)
    Long getCommentAuthorId(Long commentId);

    List<Comment> findAllByItemInOrderByCreatedAsc(List<Item> items);

}
