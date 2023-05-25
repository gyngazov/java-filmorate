package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int id;

    public InMemoryFilmStorage() {
        films = new ConcurrentHashMap<>();
        id = 0;
    }

    @Override
    public int setId() {
        return ++id;
    }

    @Override
    public Film createFilm(Film film) {
        int filmId = setId();
        film.setId(filmId);
        films.put(filmId, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return films.put(film.getId(), film);
    }

    /**
     * Удаляемый фильм должен быть в бд.
     */
    @Override
    public void deleteFilm(int id) {
        getFilm(id);
        films.remove(id);
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException("Фильм с id " + id + " не найден.");
        } else {
            return films.get(id);
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        film.addLike(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        film.deleteLike(userId);
    }

    public int getFilmsCount() {
        return films.size();
    }

    public List<Film> getFilmsByPopularity(int top) {
        return films
                .values()
                .stream()
                .sorted()
                .limit(top)
                .collect(Collectors.toList());
    }
}
