package ru.practicum.shareit.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String massage) {
        super(massage);
    }

    public ValidationException(String massage, int userId, int itemId) {
        super(massage);
    }
}
