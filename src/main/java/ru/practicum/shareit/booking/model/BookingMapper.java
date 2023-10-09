package ru.practicum.shareit.booking.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;

@UtilityClass
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public BookingShortDto bookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public Booking toBooking(BookingDto bookingDto) {
        Item item = Item.builder()
                .id(bookingDto.getItemId())
                .build();

        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(item.getOwner())
                .build();
    }

    public BookingFullDto toBookingFullDto(Booking booking) {
        ItemShortDto itemDto = ItemShortDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        UserShortDto userDto = UserShortDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();

        return BookingFullDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(booking.getStatus())
                .build();
    }
}
