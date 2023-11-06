package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;

    private final EasyRandom random = new EasyRandom();

    @Test
    @DirtiesContext
    void addItemRequest() {
        ItemRequestDto itemRequestCreateDto = random.nextObject(ItemRequestDto.class);
        User owner = random.nextObject(User.class);
        owner.setId(1);
        owner.setEmail("john@example.com");
        userService.addUser(owner);

        ItemRequestFullDto itemRequestFullDto = itemRequestService.createRequest(owner.getId(), itemRequestCreateDto);

        assertEquals(1, itemRequestFullDto.getId());
        assertEquals(itemRequestCreateDto.getDescription(), itemRequestFullDto.getDescription());
    }

}
