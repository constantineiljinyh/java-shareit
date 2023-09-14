package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    Integer id;

    @NotBlank(message = "Имя не может быть пустым")
    String name;

    String description;

    @NotNull(message = "Статус не может быть null")
    Boolean available;

    @NotNull(message = "Вещь не может быть без владельца")
    User owner;

    String request;
}
