package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmStorage;
    private int id;

    public InMemoryFilmStorage() {
        filmStorage = new ConcurrentHashMap<>();
        id = 0;
    }

    @Override
    public void createFilm(Film film) {
        validateFilm(film);
        int filmId = setId();
        film.setId(filmId);
        films.put(filmId, film);
        log.info("Создан фильм " + film);
        return film;
    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public void deleteFilm(Film film) {

    }

    @Override
    public Film getFilm(int id) {
        return null;
    }

    @Override
    public Collection<Film> getFilms() {
        return null;
    }

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void deleteLike(int filmId, int userId) {

    }
}
