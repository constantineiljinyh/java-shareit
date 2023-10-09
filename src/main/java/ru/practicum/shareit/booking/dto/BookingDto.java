package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Future;
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
    @Future(message = "Время начала брони не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть заполнено")
    private LocalDateTime end;

    private Integer itemId;
}
