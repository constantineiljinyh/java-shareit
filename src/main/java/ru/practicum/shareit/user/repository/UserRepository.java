package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
@Slf4j
public class UserRepository {
    private final HashMap<Integer, User> userHashMap = new HashMap<>();

    private Integer id = 1;

    public User addUser(User user) {
        user.setId(id++);
        userHashMap.put(user.getId(), user);
        return user;
    }

    public Collection<User> getAllUsers() {
        return userHashMap.values();
    }

    public User getUserById(Integer id) {
        User user = userHashMap.get(id);
        if (user == null) {
            log.error("Попытка получить пользователя с несуществующим id");
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public User removeUser(Integer id) {
        return userHashMap.remove(id);
    }

    public User updateUser(User user) {
        User updateUser = userHashMap.get(user.getId());
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            updateUser.setEmail(user.getEmail());
        }
        return userHashMap.put(updateUser.getId(), updateUser);
    }
}
