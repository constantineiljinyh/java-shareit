package ru.practicum.shareit.item.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner());

        if (item.getLastBooking() != null) {
            builder.lastBooking(BookingMapper.bookingShortDto(item.getLastBooking()));
        }

        if (item.getNextBooking() != null) {
            builder.nextBooking(BookingMapper.bookingShortDto(item.getNextBooking()));
        }

        if (item.getComments() != null) {
            List<CommentFullDto> commentDtos = item.getComments().stream()
                    .map(CommentMapper::toCommentFullDto)
                    .collect(Collectors.toList());
            builder.comments(commentDtos);
        }

        return builder.build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }
}