package ru.yandex.practicum.filmorate.controller;
public class ExceptionResponse {
    String error;
    String description;

    public ExceptionResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
    public String getError() {
        return error;
    }
    public String getDescription() {
        return description;
    }
}