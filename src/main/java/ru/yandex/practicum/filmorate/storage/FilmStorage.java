package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void createFilm(Film film);
    void updateFilm(Film film);
    void deleteFilm(Film film);
    Film getFilm(int id);
    Collection<Film> getFilms();
    void addLike(int filmId, int userId);
    void deleteLike(int filmId, int userId);
}