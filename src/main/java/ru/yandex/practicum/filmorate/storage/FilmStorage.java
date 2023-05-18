package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    int setId();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id) throws ObjectNotFoundException;

    Film getFilm(int id) throws ObjectNotFoundException;

    Collection<Film> getFilms();

    void addLike(int filmId, int userId) throws ObjectNotFoundException;

    void deleteLike(int filmId, int userId) throws ObjectNotFoundException;

    int getFilmsCount();

    List<Film> getFilmsByPopularity(int top);

    void createGenre(Genre genre);

    void createRating(Rating rating);

    Genre getGenre(int id);

    Rating getRating(int id);
}