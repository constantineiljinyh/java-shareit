package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;
    private final EasyRandom random = new EasyRandom();

    @Test
    @DirtiesContext
    void addUser() {
        User user = random.nextObject(User.class);
        user.setEmail("john@example.com");
        user.setId(null);

        UserDto userDto = userService.addUser(user);

        assertEquals(1, user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}