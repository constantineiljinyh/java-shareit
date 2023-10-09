package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingFullDto createBooking(Integer userId, Booking booking);

    BookingFullDto updateBookingStatus(Integer userId, Integer bookingId, boolean approved);

    BookingFullDto getBookingById(Integer userId, Integer bookingId);

    List<BookingFullDto> getBookingsByBookerId(Integer bookerId, String state);

    List<BookingFullDto> getAllBookingsForItemsByOwnerId(Integer ownerId, String state);
}
