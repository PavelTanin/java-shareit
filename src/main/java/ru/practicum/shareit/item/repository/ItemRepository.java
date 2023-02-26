package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(User user);

    @Query(value = "select * from ITEMS where lower(NAME) ilike lower('%' || ?1 || '%')" +
            "or lower(DESCRIPTION) ilike lower('%' || ?1 || '%')", nativeQuery = true)
    List<Item> searchByText(String text);


    @Query(value = "select AVAILABLE from ITEMS where ID = ?1", nativeQuery = true)
    Boolean isAvailable(Long itemId);

    @Query(value = "select owner_id from items where id = ?1", nativeQuery = true)
    Long getOwnerId(Long itemId);

    @Query(value = "SELECT ID FROM items WHERE owner_id = ?1", nativeQuery = true)
    List<Long> getItemsIdsOfOwner(Long userId);
}
