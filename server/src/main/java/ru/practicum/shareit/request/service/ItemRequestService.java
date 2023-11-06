package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestFullDto createRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestFullDto> getUserRequests(int userId);

    List<ItemRequestFullDto> getAllRequests(int userId, int from, int size);

    ItemRequestFullDto getRequestById(int userId, int requestId);

}
