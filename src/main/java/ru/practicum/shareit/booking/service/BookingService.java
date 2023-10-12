package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingFullDto createBooking(int userId, Booking booking);

    BookingFullDto updateBookingStatus(int userId, int bookingId, boolean approved);

    BookingFullDto getBookingById(int userId, int bookingId);

    List<BookingFullDto> getBookingsByBookerId(int bookerId, String state);

    List<BookingFullDto> getAllBookingsForItemsByOwnerId(int ownerId, String state);
}
