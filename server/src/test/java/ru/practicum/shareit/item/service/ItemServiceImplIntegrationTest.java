package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    private final EasyRandom random = new EasyRandom();

    @Test
    @DirtiesContext
    void addItem() {
        User owner = random.nextObject(User.class);
        owner.setId(1);
        owner.setEmail("john@example.com");
        userService.addUser(owner);
        ItemDto item = random.nextObject(ItemDto.class);
        item.setId(1);
        item.setOwner(owner);
        item.setRequestId(null);
        item.setAvailable(true);

        ItemDto itemDto1 = itemService.addItem(owner.getId(), item);

        assertEquals(item.getName(), itemDto1.getName());
        assertEquals(item.getDescription(), itemDto1.getDescription());
    }
}