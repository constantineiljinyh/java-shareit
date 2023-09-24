package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class ItemRepository {
    private final HashMap<Integer, Item> itemHashMap = new HashMap<>();

    private Integer id = 1;

    public Item addItem(Item item) {
        item.setId(id++);
        itemHashMap.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        Item updateItem = itemHashMap.get(item.getId());

        if (updateItem == null) {
            log.error("Попытка обновить вещь с несуществующим id");
            throw new NotFoundException("Элемент с указанным ID не найден");
        }

        if (updateItem.getOwner().equals(item.getOwner())) {
            if (item.getName() != null && !item.getName().isBlank()) {
                updateItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                updateItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                updateItem.setAvailable(item.getAvailable());
            }
            itemHashMap.put(updateItem.getId(), updateItem);
        } else {
            log.error("Попытка обновить вещь не хозяином id {}", item.getOwner());
            throw new NotFoundException("Вы не являетесь владельцем элемента");
        }

        return updateItem;
    }

    public Item getItem(Integer itemId) {
        Item item = itemHashMap.get(itemId);
        if (item == null) {
            log.error("Попытка получить вещь с несуществующим id");
            throw new NotFoundException("Вещи с id " + itemId + " не найдено.");
        }
        return item;
    }

    public Collection<Item> getItemsByOwnerId(Integer ownerId) {
        List<Item> ownerItems = new ArrayList<>();

        for (Item item : itemHashMap.values()) {
            if (item.getOwner().equals(ownerId)) {
                ownerItems.add(item);
            }
        }
        return ownerItems;
    }

    public List<Item> searchItems(String text) {
        List<Item> matchingItems = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return matchingItems;
        }

        String searchTextLowerCase = text.toLowerCase();

        for (Item item : itemHashMap.values()) {
            String itemNameLowerCase = item.getName().toLowerCase();
            String itemDescriptionLowerCase = item.getDescription().toLowerCase();

            if (itemNameLowerCase.contains(searchTextLowerCase) || itemDescriptionLowerCase.contains(searchTextLowerCase)) {
                if (item.getAvailable()) {
                    matchingItems.add(item);
                }
            }
        }

        return matchingItems;
    }
}

