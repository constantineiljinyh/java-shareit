package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validate.Create;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    private Integer id;

    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым")
    private String description;

    @NotBlank(groups = {Create.class}, message = "Статус не может быть пустым")
    private Boolean available;

    private Integer owner;

    private ItemRequest request;
}
