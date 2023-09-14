package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    Integer id;

    @NotBlank(message = "Логин или имя не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин или имя не может содержать пробелы")
    String name;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Неправильный формат электронной почты")
    String email;
}
