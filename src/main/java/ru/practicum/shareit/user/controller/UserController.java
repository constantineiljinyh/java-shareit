package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private UserService userService;

    @PostMapping()
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(UserMapper.toUser(userDto));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public UserDto removeUser(@PathVariable("id") Integer id) {
        return userService.removeUser(id);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Integer userId, @Valid @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return userService.updateUser(UserMapper.toUser(userDto));
    }
}
