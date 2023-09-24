package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto addUser(User userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Integer id);

    UserDto removeUser(Integer id);

    UserDto updateUser(Integer userId, User user);
}
