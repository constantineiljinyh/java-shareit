package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private final EasyRandom random = new EasyRandom();

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDto> usersDto = random.objects(UserDto.class, 2).collect(Collectors.toList());
        when(userService.getAllUsers()).thenReturn(usersDto);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(usersDto.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(usersDto.get(0).getName()))
                .andExpect(jsonPath("$[0].email").value(usersDto.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(usersDto.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(usersDto.get(1).getName()))
                .andExpect(jsonPath("$[1].email").value(usersDto.get(1).getEmail()));

        Mockito.verify(userService).getAllUsers();
    }

    @Test
    void testGetUserById() throws Exception {
        UserDto userDto = random.nextObject(UserDto.class);
        userDto.setEmail("yandex@yandex.ru");
        when(userService.getUserById(userDto.getId())).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).getUserById(Mockito.anyInt());
    }

    @Test
    @DisplayName("Создание пользователя")
    void testAddUser() throws Exception {
        UserDto userDto = random.nextObject(UserDto.class);
        userDto.setEmail("yandex@yandex.ru");
        when(userService.addUser(Mockito.any(User.class))).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).addUser(Mockito.any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userDto = random.nextObject(UserDto.class);
        userDto.setEmail("yandex@yandex.ru");
        when(userService.updateUser(Mockito.anyInt(), Mockito.any(User.class))).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).updateUser(Mockito.anyInt(), Mockito.any(User.class));
    }

    @Test
    void testRemoveUser() throws Exception {
        long userId = 1;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService).removeUser(Mockito.anyInt());
    }
}
