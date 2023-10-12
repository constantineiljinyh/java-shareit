package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    private int id;

    private int bookerId;

    @NotNull(message = "Время начала бронирования должно быть заполнено")
    @FutureOrPresent(message = "Время начала брони не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть заполнено")
    private LocalDateTime end;
}
