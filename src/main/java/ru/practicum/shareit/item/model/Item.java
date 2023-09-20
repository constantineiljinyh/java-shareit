package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    Integer id;

    String name;

    String description;

    Boolean available;

    Integer owner;

    ItemRequest request;
}
