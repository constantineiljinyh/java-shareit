package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {

    private Integer id;

    @NotBlank(message = "Текст комментария не должен быть пустым")
    private String text;

    @NotBlank(message = "Комментарий не может быть без автора")
    private String authorName;

    @Past
    private LocalDateTime created;
}