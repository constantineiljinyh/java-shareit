package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private final EasyRandom random = new EasyRandom();

    @Test
    void testGetItemsByOwnerId() throws Exception {
        int userId = 1;
        List<ItemDto> itemsDto = random.objects(ItemDto.class, 2).collect(Collectors.toList());

        when(itemService.getItemsByOwnerId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemsDto.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(itemsDto.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(itemsDto.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemsDto.get(0).getAvailable()))
                .andExpect(jsonPath("$[1].id").value(itemsDto.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(itemsDto.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(itemsDto.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(itemsDto.get(1).getAvailable()));

        verify(itemService).getItemsByOwnerId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testGetItem() throws Exception {
        ItemDto itemDto = random.nextObject(ItemDto.class);
        when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        Mockito.verify(itemService).getItem(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void shouldThrowExceptionWhenGetItemByIdWithNonExistentItem() throws Exception {
        ItemDto itemDto = random.nextObject(ItemDto.class);
        when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt())).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddItem() throws Exception {
        ItemDto itemDto = random.nextObject(ItemDto.class);
        long userId = 1;
        when(itemService.addItem(Mockito.anyInt(), Mockito.any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        Mockito.verify(itemService).addItem(Mockito.anyInt(), Mockito.any(ItemDto.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenAddItem() throws Exception {
        ItemDto itemDto = random.nextObject(ItemDto.class);
        long userId = 1;
        when(itemService.addItem(Mockito.anyInt(), Mockito.any(ItemDto.class))).thenThrow(ValidationException.class);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowExceptionWhenAddItemIfDescriptionEmpty() throws Exception {
        ItemDto itemDto = random.nextObject(ItemDto.class);
        itemDto.setDescription(null);
        long userId = 1;
        when(itemService.addItem(Mockito.anyInt(), Mockito.any(ItemDto.class))).thenThrow(ConstraintViolationException.class);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto itemDto = random.nextObject(ItemDto.class);
        ItemMapper.toItem(itemDto);
        long userId = 1;

        when(itemService.updateItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Item.class))).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void testSearchItems() throws Exception {
        List<ItemDto> itemsDto = random.objects(ItemDto.class, 1).collect(Collectors.toList());
        String text = "text";
        itemsDto.get(0).setName("find " + text);
        itemsDto.get(0).setDescription("find " + text);
        when(itemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", itemsDto.get(0).getOwner().getId())
                        .param("text", text)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemsDto.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(itemsDto.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(itemsDto.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemsDto.get(0).getAvailable()));
    }

    @Test
    void addComment() throws Exception {
        long userId = 1;
        ItemDto itemDto = random.nextObject(ItemDto.class);
        User user = random.nextObject(User.class);
        CommentDto commentCreateDto = random.nextObject(CommentDto.class);
        Comment comment = CommentMapper.toComment(commentCreateDto);
        comment.setAuthorName(user);
        CommentFullDto commentFullDto = CommentMapper.toCommentFullDto(comment);
        when(itemService.addComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(CommentDto.class))).thenReturn(commentFullDto);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentCreateDto.getText()));

        Mockito.verify(itemService).addComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(CommentDto.class));
    }

}