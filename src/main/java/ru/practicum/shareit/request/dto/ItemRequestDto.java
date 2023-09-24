package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {

    private Integer id;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Пользователь не может быть null")
    private User requestor;

    @NotNull
    private LocalDateTime created;
}
