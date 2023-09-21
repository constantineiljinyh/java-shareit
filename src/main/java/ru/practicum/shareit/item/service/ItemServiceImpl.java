package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDto addItem(Integer userId, Item item) {
        item.setOwner(userId);
        if (item.getOwner() == null) {
            log.debug("Пришел запрос на создание вещи без владельца.");
            throw new ValidationException("Вещь не может быть без владельца");
        }

        userService.getUserById(item.getOwner());
        log.info(String.format("Пришел запрос на создание вещи name %s", item.getName()));
        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, Item item) {
        item.setId(itemId);
        item.setOwner(userId);
        log.info(String.format("Пришел запрос на обновление вещи id %s", item.getId()));
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemRepository.getItem(itemId);
        log.info(String.format("Пришел запрос на получение вещи name %s", item.getName()));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Integer id) {
        log.info(String.format("Пришел запрос на просмотр вещей владельцем id %s", id));
        Collection<Item> userList = itemRepository.getItemsByOwnerId(id);

        return userList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info(String.format("Пришел запрос на поиск вещей по тексту %s", text));
        List<Item> matchingItems = itemRepository.searchItems(text);
        List<ItemDto> matchingItemDto = new ArrayList<>();

        for (Item item : matchingItems) {
            matchingItemDto.add(ItemMapper.toItemDto(item));
        }

        return matchingItemDto;
    }
}

