package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private static final LocalDate FILM_EPOCH = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }
    public Film createFilm(Film film) throws ValidationException {
        validateFilm(film);
        filmStorage.createFilm(film);
        log.info("Создан фильм " + film);
        return film;
    }
    public Film updateFilm(Film film)
            throws ValidationException, ObjectNotFoundException {
        if (film == null) {
            throw new ObjectNotFoundException("Фильм не задан.");
        }
        validateFilm(film);
        Film oldFilm = getFilm(film.getId());
        filmStorage.updateFilm(film);
        log.info("Фильм " + oldFilm + " обновлен на " + film);
        return film;
    }
    public Film getFilm(int id) throws ObjectNotFoundException {
        return filmStorage.getFilm(id);
    }
    public Collection<Film> getFilms() {
        log.info("Запрошен текущий список фильмов. Всего фильмов: " + filmStorage.getFilmsCount());
        return filmStorage.getFilms();
    }
    public void addLike(int filmId, int userId) throws ObjectNotFoundException {
        filmStorage.addLike(filmId, userId);
    }
    public void deleteLike(int filmId, int userId) throws ObjectNotFoundException {
        filmStorage.deleteLike(filmId, userId);
    }
    public List<Film> getFilmsByPopularity(int top) {
        return filmStorage.getFilmsByPopularity(top);
    }
    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Не задана дата фильма.");
        } else if (film.getReleaseDate().isBefore(FILM_EPOCH)) {
            throw new ValidationException("Слишком старая дата фильма.");
        } else if (StringUtils.isBlank(film.getName())) {
            throw new ValidationException("Наименование фильма не может быть пустым.");
        } else if (film.getDuration() < 1) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
    }
}
