package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    Integer id;

    @NotBlank(message = "Описание не может быть пустым")
    String description;

    @NotNull(message = "Пользователь не может быть null")
    User requestor;

    @NotNull
    LocalDateTime created;
}