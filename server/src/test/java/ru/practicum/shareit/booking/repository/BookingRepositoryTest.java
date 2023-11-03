package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;

    private Item item1;

    private Item item2;

    private Booking booking1;

    private Booking booking2;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("username")
                .email("email@example.com")
                .build();
        userRepository.save(user);
        item1 = Item.builder()
                .owner(user)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        item2 = Item.builder()
                .owner(user)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        booking1 = Booking.builder()
                .item(item1)
                .booker(user)
                .build();
        booking2 = Booking.builder()
                .item(item2)
                .booker(user)
                .build();
        now = LocalDateTime.now();
    }

    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }


    @Test
    void testExistsBookingByIdAndStatusNot() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now);
        booking1.setEnd(now.plusHours(1));

        bookingRepository.save(booking1);

        boolean exists = bookingRepository.existsBookingByIdAndStatusNot(booking1.getId(), Status.APPROVED);
        assertTrue(exists);
    }

    @Test
    void testExistsBookingByBookerIdAndItemIdAndStatusAndStartBefore() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now);
        booking1.setEnd(now.plusHours(1));
        bookingRepository.save(booking1);

        boolean exists = bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(
                user.getId(), item1.getId(), Status.WAITING, LocalDateTime.now().plusMinutes(30));

        assertTrue(exists);
    }

    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now);
        bookingRepository.save(booking1);

        booking2.setStatus(Status.WAITING);
        booking2.setStart(now.plusDays(1));
        booking2.setEnd(now.plusDays(2));

        bookingRepository.save(booking2);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), PageRequest.of(0, 10));
        List<Booking> bookingList = bookings.getContent();

        assertEquals(2, bookingList.size());
        assertTrue(bookingList.contains(booking1));
        assertTrue(bookingList.contains(booking2));
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDesc() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now);
        bookingRepository.save(booking1);

        booking2.setStatus(Status.WAITING);
        booking2.setStart(now.plusDays(1));
        booking2.setEnd(now.plusDays(2));

        bookingRepository.save(booking2);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING, PageRequest.of(0, 10));

        assertEquals(2, bookings.getTotalElements());

        assertTrue(bookings.getContent().contains(booking1));
        assertTrue(bookings.getContent().contains(booking2));
    }

    @Test
    void testFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        booking1.setStatus(Status.APPROVED);
        booking1.setStart(start);
        booking1.setEnd(end);
        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), end, start, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking1));
    }

    @Test
    void testFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime end = LocalDateTime.now();

        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(end);
        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void testFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime start = now.plusDays(1);

        booking1.setStatus(Status.WAITING);
        booking1.setStart(start);
        booking1.setEnd(now.plusDays(2));

        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                user.getId(), now, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking1));
    }

    @Test
    void testFindAllByItem_OwnerIdOrderByStartDesc() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.plusDays(1));

        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(
                item1.getOwner().getId(), PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking1));
    }

    @Test
    void testFindAllByItem_OwnerIdAndStatusOrderByStartDesc() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.plusDays(1));
        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                item1.getOwner().getId(), Status.WAITING, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking1));
    }

    @Test
    void testFindAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime start = currentDateTime.minusDays(1);
        LocalDateTime end = currentDateTime.plusDays(1);

        booking1.setStatus(Status.WAITING);
        booking1.setStart(start);
        booking1.setEnd(end);

        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                item1.getOwner().getId(), currentDateTime, currentDateTime, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking1));
    }

    @Test
    void testFindAllByItem_OwnerIdAndEndBeforeOrderByStartDesc() {
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now);
        booking1.setEnd(now);

        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                item1.getOwner().getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void testFindAllByItem_OwnerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime start = now.minusDays(2);

        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.plusDays(1));
        booking1.setEnd(now.plusDays(2));

        bookingRepository.save(booking1);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(
                item1.getOwner().getId(), start, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking1));
    }

    @Test
    void testFindLastBookingByOwnerId() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        booking1.setStatus(Status.REJECTED);
        booking1.setStart(currentTime.minusDays(1));
        booking1.setEnd(currentTime.minusHours(23));

        bookings.add(booking1);

        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findLastBookingByOwnerId(item1.getId(), item1.getOwner().getId(), Status.APPROVED, currentTime);

        assertTrue(foundBookings.contains(booking1));
    }

    @Test
    void testFindNextBookingByOwnerId() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        booking1.setStatus(Status.REJECTED);
        booking1.setStart(currentTime.plusDays(1));
        booking1.setEnd(currentTime.plusDays(2));

        bookings.add(booking1);

        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findNextBookingByOwnerId(item1.getId(), item1.getOwner().getId(), Status.APPROVED, currentTime);

        assertTrue(foundBookings.contains(booking1));
    }
}