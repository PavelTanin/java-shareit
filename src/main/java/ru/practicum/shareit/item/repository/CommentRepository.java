package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> getCommentsByItem_IdOrderByIdAsc(Long itemId);

    @Query(value = "SELECT COUNT(*) FROM comments WHERE ITEM_ID = ?1", nativeQuery = true)
    Long isItemHaveComments(Long itemId);

    @Query(value = "select author from comments where id = ?1", nativeQuery = true)
    Long getCommentAuthorId(Long commentId);

}
