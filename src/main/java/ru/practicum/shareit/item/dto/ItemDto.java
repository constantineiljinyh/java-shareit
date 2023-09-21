package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validate.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    Integer id;

    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым")
    String name;

    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым")
    String description;

    @NotNull(groups = {Create.class}, message = "Статус не может быть пустым")
    Boolean available;

    Integer owner;

    ItemRequest request;
}
