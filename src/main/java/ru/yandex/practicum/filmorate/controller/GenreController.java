package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Validated
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) throws SQLException {
        return filmService.getGenre(id);
    }

    @PostMapping
    public Genre createGenre(@Valid @RequestBody Genre genre) {
        return filmService.createGenre(genre);
    }

    @GetMapping
    public Collection<Genre> getGenres() {
        return filmService.getGenres();
    }
}
