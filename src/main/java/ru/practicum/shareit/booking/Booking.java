package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    Integer id;

    @NotNull
    @FutureOrPresent(message = "Время начала брони не может быть в прошлом")
    LocalDateTime start;

    @NotNull
    @FutureOrPresent(message = "Время конца брони не может быть в прошлом")
    LocalDateTime end;

    @NotNull(message = "Вещь не может быть null")
    Item item;

    @NotNull(message = "Пользователь не может быть null")
    User booker;

    Status status;
}
