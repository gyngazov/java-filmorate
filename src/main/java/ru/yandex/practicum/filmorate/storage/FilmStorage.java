package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    int setId();

    int createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(int id) throws ObjectNotFoundException;

    Collection<Film> getFilms();

    void addLike(int filmId, int userId) throws ObjectNotFoundException;

    void deleteLike(int filmId, int userId) throws ObjectNotFoundException;

    int getFilmsCount();

    List<Film> getFilmsByPopularity(int top);

    Genre getGenre(int id) throws SQLException;

    Collection<Genre> getGenres();

    void addFilmGenre(int filmId, int genreId);

    void deleteFilmGenre(int filmId, int genreId);

    Collection<Mpa> getMpas();

    Mpa getMpa(int id) throws SQLException;
}