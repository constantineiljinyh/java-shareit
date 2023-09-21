package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Integer userId, Item item);

    ItemDto updateItem(Integer itemId, Integer userId, Item item);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getItemsByOwnerId(Integer id);

    List<ItemDto> searchItems(String text);
}

