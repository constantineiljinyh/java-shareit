package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class ItemRequestFullDto {

    private Integer id;

    @NotBlank
    private String description;

    @NotNull
    private UserShortDto requestor;

    @NotNull
    private LocalDateTime created;

    private List<ItemRequestDtoCreate> items;
}
