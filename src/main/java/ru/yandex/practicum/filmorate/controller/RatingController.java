package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Validated
public class RatingController {
    private final FilmService filmService;

    @Autowired

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable int id) {
        return filmService.getRating(id);
    }
}
