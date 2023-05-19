package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Validated
public class RatingController {
    private final FilmService filmService;

    @Autowired
    public RatingController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable int id) throws SQLException {
        return filmService.getRating(id);
    }

    @PostMapping
    public Rating createRating(@Valid @RequestBody Rating rating) {
        return filmService.createRating(rating);
    }

    @GetMapping
    public Collection<Rating> getRatings() {
        return filmService.getRatings();
    }
}
