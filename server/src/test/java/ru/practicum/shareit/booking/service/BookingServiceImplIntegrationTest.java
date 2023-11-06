package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    private final EasyRandom random = new EasyRandom();

    @Test
    @DirtiesContext
    void addBooking() {

        BookingDto bookingCreateDto = random.nextObject(BookingDto.class);
        bookingCreateDto.setId(null);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        User owner = random.nextObject(User.class);
        owner.setId(1);
        owner.setEmail("john@example.com");
        userService.addUser(owner);

        User booker = random.nextObject(User.class);
        booker.setId(2);
        booker.setEmail("johny@example.com");
        userService.addUser(booker);

        ItemDto item = random.nextObject(ItemDto.class);
        item.setId(1);
        item.setOwner(owner);
        item.setRequestId(null);
        item.setAvailable(true);

        ItemDto itemSave = itemService.addItem(owner.getId(), item);
        bookingCreateDto.setItemId(itemSave.getId());

        BookingFullDto bookingFullDto = bookingService.createBooking(booker.getId(), BookingMapper.toBooking(bookingCreateDto));

        assertEquals(1, bookingFullDto.getId());
        assertEquals(bookingCreateDto.getItemId(), bookingFullDto.getItem().getId());
        assertEquals(bookingCreateDto.getStart(), bookingFullDto.getStart());
        assertEquals(bookingCreateDto.getEnd(), bookingFullDto.getEnd());
    }
}
