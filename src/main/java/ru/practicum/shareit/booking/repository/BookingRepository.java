package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    boolean existsBookingByIdAndStatusNot(Integer bookingId, Status status);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(Integer userId, Integer itemId, Status status, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer userId, Status status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Integer userId);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Integer userId, Status status);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime start);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastBookingByOwnerId(Integer itemId, Integer bookerId, Status status, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextBookingByOwnerId(Integer itemId, Integer bookerId, Status status, LocalDateTime currentTime);
}
