package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDtoCreate {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;
}
