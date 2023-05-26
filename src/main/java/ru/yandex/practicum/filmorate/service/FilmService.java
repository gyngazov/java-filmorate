package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage,
                       @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public Film createFilm(Film film) {
        film.setId(filmStorage.createFilm(film));
        log.info("Создан фильм {}.", film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film oldFilm = getFilm(film.getId());
        if (oldFilm == null) {
            throw new ObjectNotFoundException("Фильм с " + film.getId() + " не найден.");
        }
        film = filmStorage.updateFilm(film);
        log.info("Фильм {} обновлен на {}.", oldFilm, film);
        return film;
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм с " + id + " не найден.");
        }
        return film;
    }

    public Collection<Film> getFilms() {
        log.info("Запрошен текущий список фильмов. Всего фильмов: {}.", filmStorage.getFilmsCount());
        return filmStorage.getFilms();
    }

    public void addLike(int filmId, int userId) {
        if (!userStorage.isExisting("films", filmId)) {
            throw new ObjectNotFoundException("Фильм с " + filmId + " не найден.");
        } else if (!userStorage.isExisting("users", userId)) {
            throw new ObjectNotFoundException("Пользователь с " + userId + " не найден.");
        } else {
            filmStorage.addLike(filmId, userId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (!userStorage.isExisting("films", filmId)) {
            throw new ObjectNotFoundException("Фильм с " + filmId + " не найден.");
        } else if (!userStorage.isExisting("users", userId)) {
            throw new ObjectNotFoundException("Пользователь с " + userId + " не найден.");
        } else {
            filmStorage.deleteLike(filmId, userId);
        }
    }

    public List<Film> getFilmsByPopularity(int top) {
        return filmStorage.getFilmsByPopularity(top);
    }

    public Genre getGenre(int id) throws SQLException {
        Genre genre = filmStorage.getGenre(id);
        if (genre == null) {
            throw new ObjectNotFoundException("Жанр с id " + id + " не найден.");
        }
        return genre;
    }

    public void addFilmGenre(int filmId, int genreId) {
        if (!userStorage.isExisting("films", filmId)) {
            throw new ObjectNotFoundException("Фильм с id " + filmId + " не найден.");
        } else if (!userStorage.isExisting("genres", genreId)) {
            throw new ObjectNotFoundException("Жанр с id " + genreId + " не найден.");
        } else {
            filmStorage.addFilmGenre(filmId, genreId);
        }
    }

    public Collection<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Mpa getMpa(int id) throws SQLException {
        Mpa mpa = filmStorage.getMpa(id);
        if (mpa == null) {
            throw new ObjectNotFoundException("Рейтинг с id " + id + " не найден.");
        } else {
            return mpa;
        }
    }

    public Collection<Mpa> getMpas() {
        return filmStorage.getMpas();
    }

    public void deleteFilmGenre(int filmId, int genreId) {
        if (!userStorage.isExisting("films", filmId)) {
            throw new ObjectNotFoundException("Фильм с id " + filmId + " не найден.");
        } else if (!userStorage.isExisting("genres", genreId)) {
            throw new ObjectNotFoundException("Жанр с id " + genreId + " не найден.");
        } else {
            filmStorage.deleteFilmGenre(filmId, genreId);
        }
    }

}
