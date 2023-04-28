package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidationError(final RuntimeException re) {
        log.debug("Ошибка валидации {}.", re.getMessage(), re);
        return new ExceptionResponse("Ошибка валидации", re.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleAbsentObject(final RuntimeException re) {
        log.debug("Не найден объект {}.", re.getMessage(), re);
        return new ExceptionResponse("Не найден объект", re.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(final Throwable t) {
        log.debug("Возникло исключение {}.", t.getMessage(), t);
        return new ExceptionResponse("Возникло исключение", t.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleConstraints(final ConstraintViolationException cve) {
        log.debug("Ошибка параметра запроса {}.", cve.getMessage(), cve);
        return new ExceptionResponse("Ошибка параметра запроса", cve.getMessage());
    }
}