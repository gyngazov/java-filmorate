package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int setId() {
        return 0;
    }

    @Override
    public Film createFilm(Film film) {
        String insertFilm = "insert into films(name, description, release_date, duration, rating_id) "
                + "values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertFilm
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRating()
        );
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void deleteFilm(int id) {
        String del = "delete from films where id=?";
        jdbcTemplate.update(del, id);
    }

    @Override
    public Film getFilm(int id) throws ObjectNotFoundException {
        return null;
    }

    @Override
    public Collection<Film> getFilms() {
        return null;
    }

    @Override
    public void addLike(int filmId, int userId) throws ObjectNotFoundException {

    }

    @Override
    public void deleteLike(int filmId, int userId) throws ObjectNotFoundException {

    }

    @Override
    public int getFilmsCount() {
        return 0;
    }

    @Override
    public List<Film> getFilmsByPopularity(int top) {
        return null;
    }

    @Override
    public void createGenre(Genre genre) {
        String insertGenre = "insert into genres(name) values(?)";
        jdbcTemplate.update(insertGenre, genre.getName());
    }

    @Override
    public void createRating(Rating rating) {
        String insertRating = "insert into ratings(name) values(?)";
        jdbcTemplate.update(insertRating, rating.getName());
    }

    @Override
    public Genre getGenre(int id) {
        String selectGenre = "select id, name from genres where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectGenre, id);
        if (rs.next()) {
            return new Genre(rs.getInt("id"), rs.getString("name"));
        } else {
            return null;
        }
    }

    @Override
    public Rating getRating(int id) {
        String selectRating = "select id, name from ratings where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectRating, id);
        if (rs.next()) {
            return new Rating(rs.getInt("id"), rs.getString("name"));
        } else {
            return null;
        }
    }
}
