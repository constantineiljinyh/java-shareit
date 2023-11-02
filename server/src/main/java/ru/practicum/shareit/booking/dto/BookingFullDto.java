package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFullDto {
    private Integer id;

    @NotNull(message = "Время начала бронирования должно быть заполнено")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть заполнено")
    private LocalDateTime end;

    @NotNull
    private ItemShortDto item;

    @NotNull
    private UserShortDto booker;

    @NotNull
    private Status status;
}