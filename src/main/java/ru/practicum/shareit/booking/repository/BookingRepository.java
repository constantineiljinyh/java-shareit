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

    boolean existsBookingByIdAndStatusNot(int bookingId, Status status);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(int userId, int itemId, Status status, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int userId, Status status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(int userId);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(int userId, Status status);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastBookingByOwnerId(int itemId, int bookerId, Status status, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextBookingByOwnerId(int itemId, int bookerId, Status status, LocalDateTime currentTime);
}
