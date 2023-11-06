package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private final EasyRandom random = new EasyRandom();

    private final LocalDateTime yesterday = LocalDateTime.now();

    private final LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    private User user;

    private Item item;

    private User booker;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
        user = random.nextObject(User.class);
        item = random.nextObject(Item.class);
        booker = random.nextObject(User.class);

    }

    @Test
    void testCreateBooking() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, tomorrow, item, user, Status.WAITING);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(itemService.getItem(userId, booking.getItem().getId())).thenReturn(itemDto);
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);
        BookingFullDto bookingFromDb = bookingService.createBooking(userId, booking);

        assertEquals(bookingFullDto, bookingFromDb);
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfBookingStartIsNull() {
        int userId = 1;
        Booking booking = new Booking(1, null, tomorrow, item, user, Status.WAITING);
        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfBookingEndIsNull() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, null, item, user, Status.WAITING);
        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfBookingStartIsAfterBookingEnd() {
        int userId = 1;
        Booking booking = new Booking(1, tomorrow, yesterday, item, user, Status.WAITING);
        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfBookingStartIsEqualBookingEnd() {
        int userId = 1;
        Booking booking = new Booking(1, tomorrow, tomorrow, item, user, Status.WAITING);
        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfItemDoesNotExist() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, tomorrow, item, user, Status.WAITING);

        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt())).thenThrow(NotFoundException.class);


        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfUserDoesNotExist() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, tomorrow, item, user, Status.WAITING);

        when(userService.getUserById(userId)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfOwnerIsEqualBooker() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, tomorrow, item, user, Status.WAITING);

        booking.setItem(item);
        user.setId(1);
        item.setOwner(user);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(itemService.getItem(userId, booking.getItem().getId())).thenReturn(itemDto);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowValidationExceptionWhenOwnerNotFound() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, tomorrow, item, user, Status.WAITING);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setOwner(null);
        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(itemService.getItem(userId, booking.getItem().getId())).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void shouldThrowExceptionWhenAddBookingIfItemNotAvailable() {
        int userId = 1;
        Booking booking = new Booking(1, yesterday, tomorrow, item, user, Status.WAITING);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setAvailable(false);


        when(userService.getUserById(userId)).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(itemService.getItem(userId, booking.getItem().getId())).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, booking));
    }

    @Test
    void testUpdateBookingStatus() {
        Booking booking = random.nextObject(Booking.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        booking.setStatus(Status.WAITING);
        BookingFullDto bookingFullDtoFromMethod = bookingService.updateBookingStatus(user.getId(), booking.getId(), true);
        assertEquals(Status.APPROVED, bookingFullDtoFromMethod.getStatus());

        booking.setStatus(Status.WAITING);
        bookingFullDtoFromMethod = bookingService.updateBookingStatus(user.getId(), booking.getId(), false);
        assertEquals(Status.REJECTED, bookingFullDtoFromMethod.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenApprovedOrRejectedBookingIfBookingDoesNotExist() {
        Booking booking = random.nextObject(Booking.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(Mockito.anyInt())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(user.getId(), booking.getId(), true));
    }

    @Test
    void shouldThrowExceptionWhenApprovedOrRejectedBookingIfStatusChangeIsNotPossible() {
        Booking booking = random.nextObject(Booking.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsBookingByIdAndStatusNot(Mockito.anyInt(), Mockito.any(Status.class))).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.updateBookingStatus(user.getId(), booking.getId(), true));
    }

    @Test
    void shouldThrowExceptionWhenApprovedOrRejectedBookingIfItemDoesNotBelongConfirmingUser() {
        Booking booking = random.nextObject(Booking.class);
        booking.setItem(item);
        booking.setBooker(booker);


        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.existsBookingByIdAndStatusNot(Mockito.anyInt(), Mockito.any(Status.class))).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(user.getId(), booking.getId(), true));
    }

    @Test
    void getBookingById() {
        Booking booking = random.nextObject(Booking.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));

        BookingFullDto bookingFullDto = BookingMapper.toBookingFullDto(booking);
        BookingFullDto bookingFromDb = bookingService.getBookingById(booker.getId(), booking.getId());

        assertEquals(bookingFullDto, bookingFromDb);
    }

    @Test
    void shouldThrowExceptionWhenGetBookingByIdIfBookingDoesNotExist() {
        Booking booking = random.nextObject(Booking.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(booker);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booker.getId(), booking.getId()));
    }

    @Test
    @DisplayName("Получение ошибки при получении бронирования по id, когда нет доступа для просмотра бронирования")
    void shouldThrowExceptionWhenGetBookingByIdIfNoAccessToViewBooking() {
        Booking booking = random.nextObject(Booking.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1, booking.getId()));
    }

    @Test
    void getBookingsByBookerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getBookingsByBookerId(booker.getId(), "ALL", 0, 5));
    }

    @Test
    void getCurrentBookingsByBookerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getBookingsByBookerId(booker.getId(), "CURRENT", 0, 5));
    }

    @Test
    void getPastBookingsByBookerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getBookingsByBookerId(booker.getId(), "PAST", 0, 5));
    }

    @Test
    void getFutureBookingsByBookerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getBookingsByBookerId(booker.getId(), "FUTURE", 0, 5));
    }

    @Test
    void getBookingsWithStatusIsWaitingByBookerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getBookingsByBookerId(booker.getId(), "WAITING", 0, 5));
    }

    @Test
    void getBookingsWithStatusIsRejectedByBookerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getBookingsByBookerId(booker.getId(), "REJECTED", 0, 5));
    }

    @Test
    void shouldThrowExceptionWhenGetBookingsWithIsUnsupportedStatusByBookerId() {
        when(userService.getUserById(booker.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(booker));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingsByBookerId(booker.getId(), "UNSUPPORTED STATUS", 0, 5));
    }

    @Test
    void testCheckSize() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingsByBookerId(booker.getId(), "UNSUPPORTED STATUS", -1, 0));
    }

    @Test
    void getAllBookingsForItemsByOwnerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "ALL", 0, 5));
    }

    @Test
    void getAllCurrentBookingsForItemsByOwnerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "CURRENT", 0, 5));
    }

    @Test
    void getAllPastBookingsForItemsByOwnerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "PAST", 0, 5));
    }

    @Test
    void getAllFutureBookingsForItemsByOwnerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "FUTURE", 0, 5));
    }

    @Test
    void getAllBookingsForItemsWithStatusIsWaitingByOwnerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "WAITING", 0, 5));
    }

    @Test
    void getAllBookingsForItemsWithStatusIsRejectedByOwnerId() {
        item.setOwner(user);
        List<Booking> bookings = random.objects(Booking.class, 5)
                .peek(booking -> {
                    booking.setBooker(booker);
                    booking.setItem(item);
                })
                .collect(Collectors.toList());
        List<BookingFullDto> bookingsFullDto = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));
        when(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(bookings));

        assertEquals(bookingsFullDto, bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "REJECTED", 0, 5));
    }

    @Test
    void shouldThrowExceptionWhenGetBookingsWithIsUnsupportedStatusByOwnerId() {
        when(userService.getUserById(user.getId())).thenReturn(UserMapper.INSTANCE.toUserDto(user));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getAllBookingsForItemsByOwnerId(user.getId(), "UNSUPPORTED STATUS", 0, 5));
    }

}