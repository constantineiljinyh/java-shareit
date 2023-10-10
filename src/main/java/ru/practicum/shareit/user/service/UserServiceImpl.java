package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addUser(User user) {
        log.info("Пришел запрос на создание пользователя name {}", user.getName());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Пришел запрос на получение всех пользователей.");
        Collection<User> userList = userRepository.findAll();

        return userList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int id) {
        log.info("Пришел запрос на получение пользователя id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto removeUser(int id) {
        log.info("Пришел запрос на удаление пользователя id {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        userRepository.delete(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(int userId, User user) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        User existingUser = optionalUser.get();
        checkUserByIdAndEmail(existingUser);
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        log.info("Пользователь с ID {} успешно обновлен", userId);
        return UserMapper.toUserDto(userRepository.save(existingUser));
    }

    public void checkUserByIdAndEmail(User user) {
        Collection<User> users = userRepository.findAll();
        List<User> collect = users.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            log.error("Пользователь с id {} и email {} уже существует", user.getId(), user.getEmail());
            throw new ConflictException("Пользователь с таким email и id уже существует");
        }
    }
}

