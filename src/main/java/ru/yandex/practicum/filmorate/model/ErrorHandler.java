package ru.yandex.practicum.filmorate.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controllers")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidationError(final ValidationException ve) {
        return new ExceptionResponse("Ошибка валидации", ve.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleAbsentObject(final ObjectNotFoundException onfe) {
        return new ExceptionResponse("Не найден объект", onfe.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(final Exception e) {
        return new ExceptionResponse("Возникло исключение", e.getMessage());
    }
}