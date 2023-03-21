package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner_Id(Long userId, Pageable pageable);

    List<Item> findAllByRequestInOrderByIdAsc(List<ItemRequest> requests);

    @Query(value = "select * from ITEMS where lower(NAME) ilike lower('%' || ?1 || '%')" +
            "or lower(DESCRIPTION) ilike lower('%' || ?1 || '%')", nativeQuery = true)
    List<Item> searchByText(String text, Pageable pageable);

    @Query(value = "select owner_id from items where id = ?1", nativeQuery = true)
    Long getOwnerId(Long itemId);

    @Query(value = "SELECT ID FROM items WHERE owner_id = ?1", nativeQuery = true)
    List<Long> getItemsIdsOfOwner(Long userId);

}
