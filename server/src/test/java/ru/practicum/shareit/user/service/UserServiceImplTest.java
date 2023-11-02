package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
        user = new User();
        user.setId(1);
        user.setName("John");
        user.setEmail("john@example.com");
    }

    @Test
    void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto userDto = userService.addUser(user);

        assertNotNull(userDto);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        User user2 = new User();
        user2.setId(2);
        user2.setName("Alice");
        user2.setEmail("alice@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> userDtos = userService.getAllUsers();

        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());

        UserDto userDto1 = userDtos.get(0);
        assertEquals(user.getId(), userDto1.getId());
        assertEquals(user.getName(), userDto1.getName());
        assertEquals(user.getEmail(), userDto1.getEmail());

        UserDto userDto2 = userDtos.get(1);
        assertEquals(user2.getId(), userDto2.getId());
        assertEquals(user2.getName(), userDto2.getName());
        assertEquals(user2.getEmail(), userDto2.getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdValid() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(userId);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testRemoveUserValid() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.removeUser(userId);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).delete(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testRemoveUserNotFound() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.removeUser(userId);
        });

        verify(userRepository, times(0)).delete(any(User.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserValid() {
        int userId = 1;

        User updatedUser = User.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.updateUser(userId, updatedUser);

        assertNotNull(userDto);
        assertEquals(updatedUser.getName(), userDto.getName());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserNotFound() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(userId, new User());
        });

        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserConflict() {
        User existingUser2 = new User();
        existingUser2.setId(2);
        existingUser2.setName("Alice");
        existingUser2.setEmail("alice@example.com");

        User updatedUser = new User();
        updatedUser.setId(2);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("john@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user, existingUser2));
        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.of(updatedUser));

        assertThrows(ConflictException.class, () -> {
            userService.updateUser(updatedUser.getId(), updatedUser);
        });

        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).findById(updatedUser.getId());
    }

    @Test
    void testUpdateUserName() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.updateUser(user.getId(), updatedUser);

        assertEquals(updatedUser.getName(), user.getName());
        assertEquals(user.getEmail(), "john@example.com");
    }

    @Test
    void testUpdateUserEmail() {

        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.updateUser(user.getId(), updatedUser);

        assertEquals(user.getName(), "John");
        assertEquals(updatedUser.getEmail(), user.getEmail());
    }
}