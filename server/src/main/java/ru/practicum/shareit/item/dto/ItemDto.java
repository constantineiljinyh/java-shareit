package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentFullDto> comments;

    private Integer requestId;
}
