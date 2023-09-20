package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(User userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Integer id);

    UserDto removeUser(Integer id);

    UserDto updateUser(User user);
}
