package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film createFilm(Film film) {
        filmStorage.createFilm(film);
        log.info("Создан фильм {}.", film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film oldFilm = getFilm(film.getId());
        filmStorage.updateFilm(film);
        log.info("Фильм {} обновлен на {}.", oldFilm, film);
        return film;
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getFilms() {
        log.info("Запрошен текущий список фильмов. Всего фильмов: {}.", filmStorage.getFilmsCount());
        return filmStorage.getFilms();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getFilmsByPopularity(int top) {
        return filmStorage.getFilmsByPopularity(top);
    }
}
