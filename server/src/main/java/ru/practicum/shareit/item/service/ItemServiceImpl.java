package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(itemDto.getRequestId());
            if (optionalItemRequest.isPresent()) {
                ItemRequest itemRequest = optionalItemRequest.get();
                item.setRequest(itemRequest);
            } else {
                throw new NotFoundException("Запрос id " + itemDto.getRequestId() + " не найден");
            }
        }
        User user = UserMapper.toUser(userService.getUserById(userId));
        item.setOwner(user);
        log.info("Пришел запрос на создание вещи name {}", item.getName());
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, Item item) {
        item.setId(itemId);
        User user = UserMapper.toUser(userService.getUserById(userId));
        item.setOwner(user);
        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            log.error("Вещь с ID {} не найдена", itemId);
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена");
        }
        Item existingItem = optionalItem.get();

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        log.info("Вещь с ID {} успешно обновлена", itemId);
        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        log.info("Пришел запрос на получение вещи {} пользователем {}", itemId, userId);
        LocalDateTime currentTime = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь id " + itemId + " не найдена"));

        List<Booking> lastBooking = bookingRepository.findLastBookingByOwnerId(itemId, userId, Status.REJECTED, currentTime);
        List<Booking> nextBooking = bookingRepository.findNextBookingByOwnerId(itemId, userId, Status.REJECTED, currentTime);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        item.setComments(comments);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setLastBooking(
                lastBooking.stream()
                        .findFirst()
                        .map(BookingMapper::bookingShortDto)
                        .orElse(null)
        );
        itemDto.setNextBooking(
                nextBooking.stream()
                        .findFirst()
                        .map(BookingMapper::bookingShortDto)
                        .orElse(null)
        );

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Integer userId, int from, int size) {
        log.info("Пришел запрос на получение владельцем {} всех вещей", userId);
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from, size);
        Page<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, pageable);

        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            List<Booking> lastBooking = bookingRepository.findLastBookingByOwnerId(item.getId(), userId, Status.REJECTED, currentTime);
            List<Booking> nextBooking = bookingRepository.findNextBookingByOwnerId(item.getId(), userId, Status.REJECTED, currentTime);
            item.setComments(commentRepository.findAllByItemId(item.getId()));

            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (!lastBooking.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.bookingShortDto(lastBooking.get(0)));
            }
            if (!nextBooking.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.bookingShortDto(nextBooking.get(0)));
            }
            itemDtos.add(itemDto);
        }

        return itemDtos;
    }

    @Transactional
    @Override
    public CommentFullDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        log.info("Пришел запрос на создание комментария, пользователем {} к вещи {}", userId, itemId);
        LocalDateTime currentTime = LocalDateTime.now();
        checkBookingByItemAndUserAndStatusAndPast(userId, itemId);
        User author = UserMapper.toUser(userService.getUserById(userId));
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Item item = optionalItem.orElseThrow(() -> new NotFoundException("Вещь id " + itemId + " не найдена"));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthorName(author);
        comment.setItem(item);
        comment.setCreated(currentTime);
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        log.info("Пришел запрос на поиск вещей по тексту {}", text);
        if (text == null || text.isBlank()) {
            log.error("Пришел запрос на поиск вещей без текста");
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from, size);
        return itemRepository.searchItems(text, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByRequestId(int requestId) {
        return itemRepository.findByRequestId(requestId);
    }

    private void checkBookingByItemAndUserAndStatusAndPast(int userId, int itemId) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (!bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, Status.APPROVED, currentTime)) {
            log.error("Нет бронирований с такой вещью {} и пользователем {}", itemId, userId);
            throw new ValidationException("Ошибка бронирований с вещью {} и пользователем {}", itemId, userId);
        }
    }
}

