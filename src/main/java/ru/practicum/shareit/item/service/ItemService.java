package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addItem(int userId, Item item);

    ItemDto updateItem(int itemId, int userId, Item item);

    ItemDto getItem(int userId, int itemId);

    List<ItemDto> getItemsByOwnerId(int id);

    List<ItemDto> searchItems(String text);

    CommentFullDto addComment(int userId, int itemId, CommentDto commentDto);
}

