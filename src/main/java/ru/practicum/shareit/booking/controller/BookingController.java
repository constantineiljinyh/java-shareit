package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingFullDto addBooking(@RequestHeader(USER_ID_HEADER) int userId,
                                     @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, BookingMapper.toBooking(bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approvedOrRejectedBooking(@RequestHeader(USER_ID_HEADER) int userId,
                                                    @PathVariable int bookingId,
                                                    @RequestParam boolean approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBookingById(@RequestHeader(USER_ID_HEADER) int userId,
                                         @PathVariable int bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingFullDto> getUserBookings(@RequestHeader(USER_ID_HEADER) int userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) int userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBookingsForItemsByOwnerId(userId, state, from, size);
    }
}
