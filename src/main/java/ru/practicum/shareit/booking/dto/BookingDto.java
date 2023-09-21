package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    Integer id;

    @NotNull
    @FutureOrPresent(message = "Время начала брони не может быть в прошлом")
    LocalDateTime start;

    @NotNull
    @PastOrPresent(message = "Время конца брони не может быть в прошлом или настоящем")
    LocalDateTime end;

    @NotNull(message = "Вещь не может быть null")
    Item item;

    @NotNull(message = "Пользователь не может быть null")
    User booker;

    Status status;
}
