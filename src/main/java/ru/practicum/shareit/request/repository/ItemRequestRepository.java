package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(@Param("requestorId") int requestorId);

    Page<ItemRequest> findAllByOrderByCreatedDesc(Pageable pageable);

    ItemRequest findByIdAndRequestorId(@Param("requestId") int requestId, @Param("userId") int userId);
}
