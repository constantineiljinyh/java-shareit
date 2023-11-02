package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    boolean existsBookingByIdAndStatusNot(int bookingId, Status status);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(int userId, int itemId, Status status, LocalDateTime currentTime);

    Page<Booking> findAllByBookerIdOrderByStartDesc(@Param("userId") int userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int userId, Status status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(int userId, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(int userId, Status status, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b where b.item.id = :itemId and b.item.owner.id = :bookerId and b.status <> :status and b.start < :currentTime order by b.start desc")
    List<Booking> findLastBookingByOwnerId(
            @Param("itemId") int itemId,
            @Param("bookerId") int bookerId,
            @Param("status") Status status,
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query("select b from Booking b where b.item.id = :itemId and b.item.owner.id = :bookerId and b.status <> :status and b.start > :currentTime order by b.start")
    List<Booking> findNextBookingByOwnerId(@Param("itemId") int itemId,
                                           @Param("bookerId") int bookerId,
                                           @Param("status") Status status,
                                           @Param("currentTime") LocalDateTime currentTime
    );
}
