package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.controller.ItemController.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final EasyRandom random = new EasyRandom();

    @Test
    void testAddItemRequest() throws Exception {
        int userId = 1;
        ItemRequestDto itemRequestDto = random.nextObject(ItemRequestDto.class);
        UserDto userDto = random.nextObject(UserDto.class);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(UserMapper.toUser(userDto))
                .created(LocalDateTime.now())
                .build();
        ItemRequestFullDto itemRequestFullDto = RequestMapper.toItemRequestDto(itemRequest);

        when(itemRequestService.createRequest(userId, itemRequestDto)).thenReturn(itemRequestFullDto);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        Mockito.verify(itemRequestService).createRequest(Mockito.anyInt(), Mockito.any(ItemRequestDto.class));
    }

    @Test
    void testGetUserRequests() throws Exception {
        int requestorId = 1;
        User requestor = random.nextObject(User.class);
        requestor.setId(requestorId);
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);
        itemRequest.setRequestor(requestor);
        ItemRequestFullDto itemRequestFullDto = RequestMapper.toItemRequestDto(itemRequest);
        List<ItemRequestFullDto> itemRequests = List.of(itemRequestFullDto);
        itemRequests.get(0).setItems(null);

        when(itemRequestService.getUserRequests(Mockito.anyInt())).thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequests.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequests.get(0).getDescription()));

        Mockito.verify(itemRequestService).getUserRequests(Mockito.anyInt());
    }

    @Test
    void testGetAllRequests() throws Exception {
        int userId = 2;
        User user = random.nextObject(User.class);
        user.setId(userId);
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);
        itemRequest.setRequestor(user);
        ItemRequestFullDto itemRequestFullDto = RequestMapper.toItemRequestDto(itemRequest);
        List<ItemRequestFullDto> itemRequests = List.of(itemRequestFullDto);
        itemRequests.get(0).setItems(null);

        when(itemRequestService.getAllRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemRequests);

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequests.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequests.get(0).getDescription()));

        Mockito.verify(itemRequestService).getAllRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testGetRequestById() throws Exception {
        int requestorId = 1;
        int requestId = 1;
        ItemRequestFullDto itemRequestFullDto = random.nextObject(ItemRequestFullDto.class);

        when(itemRequestService.getRequestById(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemRequestFullDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestFullDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestFullDto.getDescription()));

        Mockito.verify(itemRequestService).getRequestById(Mockito.anyInt(), Mockito.anyInt());
    }
}