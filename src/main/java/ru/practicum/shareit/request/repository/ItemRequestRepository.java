package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorOrderByIdAsc(User user);

    Page<ItemRequest> findAllByRequestorIsNotOrderByIdAsc(User user, Pageable pageable);

    @Query(value = "SELECT REQUESTOR_ID FROM REQUESTS WHERE ID =?1", nativeQuery = true)
    Long getRequestorId(Long requestId);


}
