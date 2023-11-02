package ru.practicum.shareit.request.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.dto.UserShortDto;

@UtilityClass
public class RequestMapper {
    public ItemRequestFullDto toItemRequestDto(ItemRequest itemRequest) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(itemRequest.getRequestor().getId())
                .name(itemRequest.getRequestor().getName())
                .build();

        return ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(userShortDto)
                .build();
    }

}
