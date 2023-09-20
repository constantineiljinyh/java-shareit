package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserDto addUser(User user) {
        validateUser(user);
        checkUserByEmail(user);
        log.info(String.format("Пришел запрос на создание пользователя name %s", user.getName()));
        User user1 = userRepository.addUser(user);

        return UserMapper.toUserDto(user1);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Пришел запрос на получение всех пользователей.");
        Collection<User> userList = userRepository.getAllUsers();

        return userList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer id) {
        log.info(String.format("Пришел запрос на создание пользователя id %s", id));
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto removeUser(Integer id) {
        log.info(String.format("Пришел запрос на удаление пользователя id %s", id));
        return UserMapper.toUserDto(userRepository.removeUser(id));
    }

    @Override
    public UserDto updateUser(User user) {
        checkUserByIdAndEmail(user);
        log.info(String.format("Пришел запрос на получение пользователя id %s", user.getId()));
        User user1 = userRepository.updateUser(user);
        return UserMapper.toUserDto(user1);
    }

    private void validateUser(User user) {
        if (user == null) {
            log.debug("Пользователь равен null.");
            throw new ValidationException("Пустой пользователь");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Пользователь без имени");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Пользователь без email");
        }
    }

    public void checkUserByEmail(User user) {
        List<UserDto> collect = userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            log.error(String.format("Пользователь с email %s уже существует", user.getEmail()));
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    public void checkUserByIdAndEmail(User user) {
        Collection<User> users = userRepository.getAllUsers();
        List<User> collect = users.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            log.error(String.format("Пользователь с id %s и email %s уже существует", user.getId(), user.getEmail()));
            throw new ConflictException("Пользователь с таким email и id уже существует");
        }
    }
}

