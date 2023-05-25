package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @Autowired

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = "10") int count) {
        return filmService.getFilmsByPopularity(count);
    }

    @PutMapping("/{id}/genre/{genreId}")
    public void addFilmGenre(@PathVariable int id, @PathVariable int genreId) throws SQLException {
        filmService.addFilmGenre(id, genreId);
    }

    @DeleteMapping("/{id}/genre/{genreId}")
    public void deleteFilmGenre(@PathVariable int id, @PathVariable int genreId) throws SQLException {
        filmService.deleteFilmGenre(id, genreId);
    }
}
