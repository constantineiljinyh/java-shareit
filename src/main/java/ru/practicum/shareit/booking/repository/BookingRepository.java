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

    boolean existsBookingByIdAndStatusNot(@Param("bookingId") int bookingId, @Param("status") Status status);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(
            @Param("userId") int userId,
            @Param("itemId") int itemId,
            @Param("status") Status status,
            @Param("currentTime") LocalDateTime currentTime
    );

    Page<Booking> findAllByBookerIdOrderByStartDesc(@Param("userId") int userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            @Param("userId") int userId,
            @Param("status") Status status,
            Pageable pageable
    );

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            @Param("userId") int userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            @Param("userId") int userId,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            @Param("userId") int userId,
            @Param("start") LocalDateTime start,
            Pageable pageable
    );

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(@Param("userId") int userId, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(
            @Param("userId") int userId,
            @Param("status") Status status,
            Pageable pageable
    );

    Page<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            @Param("userId") int userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(
            @Param("userId") int userId,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    Page<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(
            @Param("userId") int userId,
            @Param("start") LocalDateTime start,
            Pageable pageable
    );


    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastBookingByOwnerId(
            @Param("itemId") int itemId,
            @Param("bookerId") int bookerId,
            @Param("status") Status status,
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextBookingByOwnerId(@Param("itemId") int itemId,
                                           @Param("bookerId") int bookerId,
                                           @Param("status") Status status,
                                           @Param("currentTime") LocalDateTime currentTime
    );
}
