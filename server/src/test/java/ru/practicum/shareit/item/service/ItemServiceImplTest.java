package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemServiceImpl itemService;

    private final EasyRandom random = new EasyRandom();

    private ItemDto itemDto;

    private UserDto userDto;

    private ItemRequest itemRequest;

    private Item existingItem;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository, itemRequestRepository);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Test item request description");

        existingItem = new Item();
        existingItem.setId(1);
        existingItem.setName("Drill");
        existingItem.setDescription("Drill Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(UserMapper.toUser(userDto));
    }

    @Test
    void testAddItem() {
        int userId = 1;

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(new Item());

        ItemDto result = itemService.addItem(userId, itemDto);

        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRequestRepository, times(1)).findById(itemDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testAddItemWithNonExistingRequest() {
        int userId = 1;

        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(userId, itemDto));

        verify(itemRequestRepository, times(1)).findById(itemDto.getRequestId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void testUpdateItem() {
        int itemId = 1;
        int userId = 1;

        itemDto.setAvailable(false);

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        ItemDto result = itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto));

        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testUpdateItemWithNonExistingItem() {
        int itemId = 1;
        int userId = 1;

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(itemId, userId, existingItem);
        });
    }

    @Test
    void testUpdateItemWithNameOnly() {
        int itemId = 1;
        int userId = 1;

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        ItemDto result = itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto));

        assertEquals(itemDto.getName(), result.getName());
        assertEquals(existingItem.getDescription(), result.getDescription());
        assertEquals(existingItem.getAvailable(), result.getAvailable());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testUpdateItemWithDescriptionOnly() {
        int itemId = 1;
        int userId = 1;

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        ItemDto result = itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto));

        assertEquals(existingItem.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(existingItem.getAvailable(), result.getAvailable());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testUpdateItemWithAvailabilityOnly() {
        int itemId = 1;
        int userId = 1;

        when(userService.getUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        ItemDto result = itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto));

        assertEquals(existingItem.getName(), result.getName());
        assertEquals(existingItem.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        verify(userService, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testGetItemById() {
        User owner = random.nextObject(User.class);
        Item item = random.nextObject(Item.class);
        Booking lastBooking = random.nextObject(Booking.class);
        Booking nextBooking = random.nextObject(Booking.class);
        List<Comment> comments = random.objects(Comment.class, 2).collect(Collectors.toList());
        item.setOwner(owner);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        item.setComments(comments);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByOwnerId(
                anyInt(), anyInt(), Mockito.any(Status.class), Mockito.any(LocalDateTime.class))).thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBookingByOwnerId(
                anyInt(), anyInt(), Mockito.any(Status.class), Mockito.any(LocalDateTime.class))).thenReturn(List.of(nextBooking));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(comments);

        assertEquals(itemDto, itemService.getItem(item.getId(), owner.getId()));
    }

    @Test
    void testGetItemsByOwnerIdWithPagination() {
        int userId = 1;
        int page = 0;
        int size = 10;

        User owner = random.nextObject(User.class);
        Booking lastBooking = random.nextObject(Booking.class);
        Booking nextBooking = random.nextObject(Booking.class);
        List<Comment> comments = random.objects(Comment.class, 2).collect(Collectors.toList());
        Page<Item> itemsPage = new PageImpl<>(random.objects(Item.class, 10)
                .peek(item -> {
                    item.setOwner(owner);
                    item.setLastBooking(lastBooking);
                    item.setNextBooking(nextBooking);
                    item.setComments(comments);
                })
                .collect(Collectors.toList()));

        when(itemRepository.findAllByOwnerIdOrderById(anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(itemsPage);
        List<ItemDto> itemsDto = itemService.getItemsByOwnerId(userId, page, size);
        assertEquals(size, itemsDto.size());
    }

    @Test
    void testAddComment() {
        UserDto user = random.nextObject(UserDto.class);
        Item item = random.nextObject(Item.class);
        CommentDto commentCreateDto = random.nextObject(CommentDto.class);
        Comment comment = CommentMapper.toComment(commentCreateDto);
        comment.setAuthorName(UserMapper.toUser(user));
        comment.setItem(item);
        LocalDateTime currentTime = LocalDateTime.now();
        comment.setCreated(currentTime);

        CommentFullDto commentFullDto = CommentMapper.toCommentFullDto(comment);
        when(bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentFullDto result = itemService.addComment(user.getId(), item.getId(), commentCreateDto);

        assertEquals(commentFullDto, result);

        verify(userService, times(1)).getUserById(eq(user.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testShouldThrowExceptionWhenAddItemIfBookingWithCurrentItemAndCurrentUserDoesNotExist() {
        CommentDto commentCreateDto = random.nextObject(CommentDto.class);
        when(bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(
                Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Status.class), Mockito.any(LocalDateTime.class))).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(1, 1, commentCreateDto));
    }

    @Test
    void testSearchItems() {
        String text = "text";

        itemDto.setComments(null);
        itemDto.setRequestId(null);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setName("Название " + text);
        itemDto.setDescription("Описание " + text);
        List<ItemDto> itemsDto = List.of(itemDto);

        List<Item> items = itemsDto.stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList());

        Page<Item> itemPage = new PageImpl<>(items);

        when(itemRepository.searchItems(Mockito.anyString(), any(Pageable.class)))
                .thenReturn(itemPage);

        List<ItemDto> searchResult = itemService.searchItems(text, 0, 5);

        assertEquals(1, searchResult.size());
        assertEquals(itemDto, searchResult.get(0));
    }

    @Test
    @DisplayName("Получение пустого списка при поиске, когда запрос пустой")
    void shouldGetEmptyListWhenRequestIsEmpty() {
        assertEquals(Collections.emptyList(), itemService.searchItems(null, 0, 5));
    }

    @Test
    void findByRequestIdTest() {
        int requestId = 1;
        List<Item> expectedItems = List.of(new Item(), new Item(), new Item());

        when(itemRepository.findByRequestId(requestId)).thenReturn(expectedItems);

        List<Item> actualItems = itemService.findByRequestId(requestId);

        assertEquals(expectedItems.size(), actualItems.size());

        verify(itemRepository, times(1)).findByRequestId(requestId);
    }
}





