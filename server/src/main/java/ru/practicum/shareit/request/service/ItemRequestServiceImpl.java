package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserService userService;

    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestFullDto createRequest(int userId, ItemRequestDto itemRequestDto) {
        UserDto userDto = userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(UserMapper.INSTANCE.toUser(userDto))
                .created(LocalDateTime.now())
                .build();

        ItemRequest saveRequest = itemRequestRepository.save(itemRequest);
        return RequestMapper.toItemRequestDto(saveRequest);
    }

    @Override
    public List<ItemRequestFullDto> getUserRequests(int userId) {
        User user = UserMapper.INSTANCE.toUser(userService.getUserById(userId));
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        List<ItemRequestFullDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> items = itemService.findByRequestId(request.getId());
            ItemRequestFullDto requestDto = RequestMapper.toItemRequestDto(request);
            requestDto.setItems(ItemMapper.toItemShortDtoList(items));
            result.add(requestDto);
        }
        return result;
    }

    @Override
    public List<ItemRequestFullDto> getAllRequests(int userId, int from, int size) {
        userService.getUserById(userId);
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> requestsPage = itemRequestRepository.findAllByOrderByCreatedDesc(pageable);

        List<ItemRequestFullDto> result = new ArrayList<>();
        for (ItemRequest request : requestsPage) {
            if (request.getRequestor().getId() != userId) {
                List<Item> items = itemService.findByRequestId(request.getId());
                ItemRequestFullDto requestDto = RequestMapper.toItemRequestDto(request);
                requestDto.setItems(ItemMapper.toItemShortDtoList(items));
                result.add(requestDto);
            }
        }
        return result;
    }

    @Override
    public ItemRequestFullDto getRequestById(int userId, int requestId) {
        userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос товара не найден с id: " + requestId));

        List<Item> items = itemService.findByRequestId(itemRequest.getId());
        ItemRequestFullDto requestDto = RequestMapper.toItemRequestDto(itemRequest);
        requestDto.setItems(ItemMapper.toItemShortDtoList(items));
        return requestDto;
    }
}
