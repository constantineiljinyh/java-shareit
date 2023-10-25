package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    private ItemRequestServiceImpl itemRequestService;

    private final EasyRandom random = new EasyRandom();


    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemService);
    }

    @Test
    void testCreateRequest() {
        int userId = 1;
        String description = "Test item request description";

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);

        when(userService.getUserById(userId)).thenReturn(userDto);

        doAnswer(invocation -> {
            ItemRequest itemRequest = invocation.getArgument(0);
            itemRequest.setId(1);
            return itemRequest;
        }).when(itemRequestRepository).save(any(ItemRequest.class));

        ItemRequestFullDto result = itemRequestService.createRequest(userId, itemRequestDto);

        verify(userService, times(1)).getUserById(userId);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(userId, result.getRequestor().getId());
        assertEquals("John", result.getRequestor().getName());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void testGetUserRequests() {
        int userId = 1;

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Test item request description");
        itemRequest.setRequestor(UserMapper.toUser(userDto));
        itemRequest.setCreated(LocalDateTime.now());

        Item item1 = Item.builder()
                .owner(UserMapper.toUser(userDto))
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .request(itemRequest)
                .build();

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(Collections.singletonList(itemRequest));
        when(itemService.findByRequestId(itemRequest.getId())).thenReturn(Collections.singletonList(item1));

        List<ItemRequestFullDto> result = itemRequestService.getUserRequests(userId);

        verify(userService, times(1)).getUserById(userId);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);

        verify(itemService, times(1)).findByRequestId(itemRequest.getId());
    }

    @Test
    void testGetAllRequests() {
        int userId = 1;
        int from = 0;
        int size = 10;

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2);
        userDto2.setName("Another User");
        userDto2.setEmail("another@example.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Test item request description");
        itemRequest.setRequestor(UserMapper.toUser(userDto2));
        itemRequest.setCreated(LocalDateTime.now());

        Item item1 = Item.builder()
                .id(1)
                .owner(UserMapper.toUser(userDto2))
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .request(itemRequest)
                .build();

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = new PageImpl<>(itemRequests, pageable, itemRequests.size());

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRequestRepository.findAllByOrderByCreatedDesc(pageable)).thenReturn(page);
        when(itemService.findByRequestId(itemRequest.getId())).thenReturn(Collections.singletonList(item1));

        List<ItemRequestFullDto> result = itemRequestService.getAllRequests(userId, from, size);

        verify(userService, times(1)).getUserById(userId);
        verify(itemRequestRepository, times(1)).findAllByOrderByCreatedDesc(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    void testGetRequestByIdValidRequest() {
        int userId = 1;
        int requestId = 1;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Test item request description");
        itemRequest.setRequestor(UserMapper.toUser(userDto));
        itemRequest.setCreated(LocalDateTime.now());

        Item item1 = Item.builder()
                .id(1)
                .owner(UserMapper.toUser(userDto))
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .request(itemRequest)
                .build();

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemService.findByRequestId(requestId)).thenReturn(Collections.singletonList(item1));

        ItemRequestFullDto result = itemRequestService.getRequestById(userId, requestId);

        verify(userService, times(1)).getUserById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemService, times(1)).findByRequestId(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void testGetRequestById_InvalidRequest() {
        int userId = 1;
        int requestId = 1;

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userService.getUserById(userId)).thenReturn(userDto);

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(userId, requestId);
        });
        verify(userService, times(1)).getUserById(userId);
    }


}