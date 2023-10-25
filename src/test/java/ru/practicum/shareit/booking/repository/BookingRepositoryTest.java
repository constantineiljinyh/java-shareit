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
    }

    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }


    @Test
    void testExistsBookingByIdAndStatusNot() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now)
                .end(now.plusHours(1))
                .build();
        bookingRepository.save(booking);

        boolean exists = bookingRepository.existsBookingByIdAndStatusNot(booking.getId(), Status.APPROVED);
        assertTrue(exists);
    }

    @Test
    void testExistsBookingByBookerIdAndItemIdAndStatusAndStartBefore() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now)
                .end(now.plusHours(1))
                .build();
        bookingRepository.save(booking);

        boolean exists = bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(
                user.getId(), item1.getId(), Status.WAITING, LocalDateTime.now().plusMinutes(30));

        assertTrue(exists);
    }

    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now.minusDays(1))
                .end(now)
                .build();
        bookingRepository.save(booking1);

        Booking booking2 = Booking.builder()
                .item(item2)
                .booker(user)
                .status(Status.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        bookingRepository.save(booking2);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), PageRequest.of(0, 10));
        List<Booking> bookingList = bookings.getContent();

        assertEquals(2, bookingList.size());
        assertTrue(bookingList.contains(booking1));
        assertTrue(bookingList.contains(booking2));
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .item(item2)
                .booker(user)
                .status(Status.WAITING)
                .start(now.minusDays(1))
                .end(now)
                .build();
        bookingRepository.save(booking1);

        Booking booking2 = Booking.builder()
                .item(item2)
                .booker(user)
                .status(Status.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        bookingRepository.save(booking2);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING, PageRequest.of(0, 10));

        assertEquals(2, bookings.getTotalElements());

        assertTrue(bookings.getContent().contains(booking1));
        assertTrue(bookings.getContent().contains(booking2));
    }

    @Test
    void testFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.APPROVED)
                .start(start)
                .end(end)
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), end, start, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking));
    }

    @Test
    void testFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();

        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now)
                .end(end)
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void testFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);

        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(start)
                .end(now.plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                user.getId(), now, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking));
    }

    @Test
    void testFindAllByItem_OwnerIdOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(
                item1.getOwner().getId(), PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking));
    }

    @Test
    void testFindAllByItem_OwnerIdAndStatusOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                item1.getOwner().getId(), Status.WAITING, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking));
    }

    @Test
    void testFindAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime start = currentDateTime.minusDays(1);
        LocalDateTime end = currentDateTime.plusDays(1);

        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(start)
                .end(end)
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                item1.getOwner().getId(), currentDateTime, currentDateTime, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking));
    }

    @Test
    void testFindAllByItem_OwnerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();

        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now)
                .end(now)
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                item1.getOwner().getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void testFindAllByItem_OwnerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(2);

        Booking booking = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(
                item1.getOwner().getId(), start, PageRequest.of(0, 10));

        assertTrue(bookings.getContent().contains(booking));
    }

    @Test
    void testFindLastBookingByOwnerId() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        Booking booking1 = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.REJECTED)
                .start(currentTime.minusDays(1))
                .end(currentTime.minusHours(23))
                .build();
        bookings.add(booking1);

        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findLastBookingByOwnerId(item1.getId(), item1.getOwner().getId(), Status.APPROVED, currentTime);

        assertTrue(foundBookings.contains(booking1));
    }

    @Test
    void testFindNextBookingByOwnerId() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        Booking booking1 = Booking.builder()
                .item(item1)
                .booker(user)
                .status(Status.REJECTED)
                .start(currentTime.plusDays(1))
                .end(currentTime.plusDays(2))
                .build();
        bookings.add(booking1);

        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findNextBookingByOwnerId(item1.getId(), item1.getOwner().getId(), Status.APPROVED, currentTime);

        assertTrue(foundBookings.contains(booking1));
    }
}