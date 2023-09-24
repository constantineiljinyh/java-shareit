package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    private Integer id;

    @NotNull
    @Future(message = "Время начала брони не может быть в прошлом")
    private LocalDateTime start;

    @NotNull
    @PastOrPresent(message = "Время конца брони не может быть в прошлом или настоящем")
    private LocalDateTime end;

    @NotNull(message = "Вещь не может быть null")
    private Item item;

    @NotNull(message = "Пользователь не может быть null")
    private User booker;

    private Status status;
}
