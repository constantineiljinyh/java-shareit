package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestFullDto createRequest(@RequestHeader(USER_ID_HEADER) int userId, @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestFullDto> getUserRequests(@RequestHeader(USER_ID_HEADER) int userId) {

        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getAllRequests(@RequestHeader(USER_ID_HEADER) int userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(defaultValue = "10") @Min(1) int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestFullDto getRequestById(@RequestHeader(USER_ID_HEADER) int userId, @PathVariable int requestId) {
        return itemRequestService.getRequestById(userId, requestId);

    }


}
