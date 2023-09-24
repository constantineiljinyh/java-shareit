package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Integer id;

    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым")
    @Pattern(groups = {Update.class}, regexp = "\\S+", message = "Логин или имя не может содержать пробелы")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Email не может быть пустым")
    @Email(groups = {Create.class, Update.class}, message = "Неправильный формат электронной почты")
    @Pattern(groups = {Update.class}, regexp = "\\S+", message = "Email или имя не может содержать пробелы")
    private String email;
}
