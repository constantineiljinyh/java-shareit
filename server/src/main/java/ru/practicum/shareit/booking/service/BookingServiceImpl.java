package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;

    @Transactional
    @Override
    public BookingFullDto createBooking(int userId, Booking booking) {
        log.info("Пришел запрос на создание пользователем {} брони", userId);
        User user = UserMapper.INSTANCE.toUser(userService.getUserById(userId));
        checkDateEndIsAfterStart(booking);
        Integer itemId = booking.getItem().getId();
        Item item = ItemMapper.toItem(itemService.getItem(userId, itemId));
        if (item.getOwner() == null) {
            log.error("Владелец вещи не найден");
            throw new ValidationException("Владелец вещи не найден");
        }
        Integer ownerId = item.getOwner().getId();

        if (ownerId.equals(userId)) {
            log.error("Попытка бронирования вещи владельцем");
            throw new NotFoundException("Бронирование невозможно. Владелец вещи и пользователь совпадают");
        }
        checkAvailabilityItem(item);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingFullDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingFullDto updateBookingStatus(int userId, int bookingId, boolean approved) {
        log.info("Пришел запрос на обновление пользователем {} брони {}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирования с таким id не существует: " + bookingId));

        if (bookingRepository.existsBookingByIdAndStatusNot(bookingId, Status.WAITING)) {
            Status status = bookingRepository.findById(bookingId).get().getStatus();
            String errorMessage = String.format("Изменение статуса бронирования запрещено, потому что у него статус \"%s\"", status);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Пользователь не является владельцем вещи.");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingFullDto(updatedBooking);
    }

    @Override
    public BookingFullDto getBookingById(int userId, int bookingId) {
        log.info("Пришел запрос на получение пользователем {} брони {}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирования с таким id не существует: " + bookingId));
        int bookerId = booking.getBooker().getId();
        int ownerId = booking.getItem().getOwner().getId();
        if (userId != bookerId && userId != ownerId) {
            log.error("Попытка просмотра бронирования не владельцем и не автором брони.");
            throw new NotFoundException("Пользователь id {} не может просматривать данное бронирование", userId);
        }

        return BookingMapper.toBookingFullDto(booking);
    }

    @Override
    public List<BookingFullDto> getBookingsByBookerId(int bookerId, String state, int from, int size) {
        checkSize(size, from);
        log.info("Пришел запрос получение списка всех бронирований текущего пользователя {} брони", bookerId);
        userService.getUserById(bookerId);
        Pageable pageable = PageRequest.of(from / size, size);
        State stateBooking = State.valueOf(state);

        Page<Booking> bookingsPage;

        switch (stateBooking) {
            case ALL:
                bookingsPage = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
                break;
            case CURRENT:
                bookingsPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookingsPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingsPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingsPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookingsPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, pageable);
                break;
            default:
                log.error("Пришел запрос с UNSUPPORTED_STATUS");
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingsPage.getContent().stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingFullDto> getAllBookingsForItemsByOwnerId(int ownerId, String state, int from, int size) {
        checkSize(size, from);
        log.info("Пришел запрос Получение списка бронирований для всех вещей текущего пользователя {} ", ownerId);
        userService.getUserById(ownerId);
        Pageable pageable = PageRequest.of(from / size, size);
        State stateBooking = State.valueOf(state);
        switch (stateBooking) {
            case ALL:
                return bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId, pageable).stream()
                        .map(BookingMapper::toBookingFullDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable
                ).stream().map(BookingMapper::toBookingFullDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingFullDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingFullDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING, pageable).stream()
                        .map(BookingMapper::toBookingFullDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED, pageable).stream()
                        .map(BookingMapper::toBookingFullDto).collect(Collectors.toList());
            default:
                log.error("Пришел запрос с UNSUPPORTED_STATUS");
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkAvailabilityItem(Item item) {
        if (!item.getAvailable()) {
            log.error("Попытка получения забронированной вещи с ID {}", item.getId());
            throw new ValidationException("Вещь с ID {} уже забронирована " + item.getId());
        }
    }

    private void checkDateEndIsAfterStart(Booking booking) {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        if (start == null || end == null) {
            String errorMessage = "Даты начала и окончания бронирования не могут быть пустыми";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        if (start.isAfter(end)) {
            String errorMessage = "Дата начала бронирования не может быть позже даты окончания бронирования";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (start.equals(end)) {
            String errorMessage = "Даты начала и окончания бронирования не могут совпадать";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkSize(int size, int from) {
        if (size < 1 || from < 0) {
            throw new ValidationException("Некорректный размер");
        }
    }
}