package ru.practicum.shareit.item.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDtoCreate;

import java.util.ArrayList;
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
        if (item.getRequest() != null) {
            builder.requestId(item.getRequest().getId());
        }

        return builder.build();
    }

    public Item toItem(ItemDto itemDto) {
        Item.ItemBuilder itemBuilder = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner());
        return itemBuilder.build();
    }

    public List<ItemRequestDtoCreate> toItemShortDtoList(List<Item> items) {
        List<ItemRequestDtoCreate> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemRequestDtoCreate itemDto = ItemRequestDtoCreate.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .requestId(item.getRequest().getId())
                    .build();
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }


}