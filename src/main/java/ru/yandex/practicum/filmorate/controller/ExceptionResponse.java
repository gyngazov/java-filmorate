package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class ExceptionResponse {
    String error;
    String description;
}