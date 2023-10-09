package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto addBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, BookingMapper.toBooking(bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approvedOrRejectedBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer bookingId,
            @RequestParam boolean approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingFullDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsForItemsByOwnerId(userId, state);
    }
}
