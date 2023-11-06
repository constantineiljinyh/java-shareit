package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorDto {
    private int code;
    private String message;
    private List<String> details;
}