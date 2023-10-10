package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class BookingDto {
    private Integer id;

    @NotNull(message = "Время начала бронирования должно быть заполнено")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @Future(message = "Время окончания бронирования не может быть в прошлом")
    @NotNull(message = "Время окончания бронирования должно быть заполнено")
    private LocalDateTime end;

    private Integer itemId;
}
