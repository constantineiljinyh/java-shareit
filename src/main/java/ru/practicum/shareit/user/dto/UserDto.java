package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    Integer id;

    @Pattern(regexp = "\\S+", message = "Логин или имя не может содержать пробелы")
    String name;

    @Email(message = "Неправильный формат электронной почты")
    String email;
}
