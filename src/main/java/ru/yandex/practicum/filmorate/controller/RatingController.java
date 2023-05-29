package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

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
    public Mpa getRating(@PathVariable int id) throws SQLException {
        return filmService.getMpa(id);
    }

    @GetMapping
    public Collection<Mpa> getRatings() {
        return filmService.getMpas();
    }
}
