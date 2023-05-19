package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int setId() {
        return 0;
    }

    @Override
    public int createFilm(Film film) {
        String insertFilm = "insert into films(name, description, release_date, duration, rating_id) "
                + "values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertFilm
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRating()
        );
        return insertFilmGenres(getFilmId(), new ArrayList<>(film.getFilmGenres()));
    }

    private int getFilmId() {
        String lastId = "select max(id) from films";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    private int insertFilmGenres(int filmId, List<Integer> genres) {
        String insertFilmGenres = "insert into film_genres(film_id, genre_id) values(?, ?)";
        jdbcTemplate.batchUpdate(insertFilmGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genres.get(i));
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        return filmId;
    }

    @Override
    public void addFilmGenre(int filmId, int genreId) {
        String insertFilmGenre = "insert into film_genres(film_id, genre_id) values(?, ?)";
        jdbcTemplate.update(insertFilmGenre, filmId, genreId);
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
    public Film getFilm(int id) {
        String queryFilm = "select * from films where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(queryFilm, id);
        Film film = rs.next() ? new Film(rs.getInt(1)
                , rs.getString(2)
                , rs.getString(3)
                , Objects.requireNonNull(rs.getDate(4)).toLocalDate()
                , rs.getInt(5)
                , rs.getInt(6)) : null;
        if (film == null) {
            return null;
        }
        film.setFilmGenres(getColumn3("film_genres", id));
        film.setUsersLikes(getColumn3("likes", id));
        return film;
    }

    /**
     * Получение лайков и жанров в виду единообразия хранения.
     *
     * @param tabName имя таблицы хранения
     */
    private Set<Integer> getColumn3(String tabName, int id) {
        String query = "select * from " + tabName + " where film_id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(query, id);
        Set<Integer> column3 = new HashSet<>();
        while (rs.next()) {
            column3.add(rs.getInt(3));
        }
        return column3;
    }

    @Override
    public Collection<Film> getFilms() {
        String queryFilms = "select * from films";
        return null;
    }

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void deleteLike(int filmId, int userId) {

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
    public int createGenre(Genre genre) {
        String insertGenre = "insert into genres(name) values(?)";
        jdbcTemplate.update(insertGenre, genre.getName());
        String lastId = "select max(id) from genres";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    @Override
    public int createRating(Rating rating) {
        String insertGenre = "insert into ratings(name) values(?)";
        jdbcTemplate.update(insertGenre, rating.getName());
        String lastId = "select max(id) from ratings";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    @Override
    public Genre getGenre(int id) {
        String selectGenre = "select id, name from genres where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectGenre, id);
        return rs.next() ? new Genre(rs.getString("name")
                , rs.getInt("id")) : null;
    }

    @Override
    public Collection<Genre> getGenres() {
        String selectGenres = "select id, name from genres";
        return jdbcTemplate.query(selectGenres
                , (rs, rowNum) -> new Genre(rs.getString("name")
                        , rs.getInt("id")));
    }

    @Override
    public Collection<Rating> getRatings() {
        String selectRatings = "select id, name from ratings";
        return jdbcTemplate.query(selectRatings
                , (rs, rowNum) -> new Rating(rs.getString("name")
                        , rs.getInt("id")));
    }

    @Override
    public Rating getRating(int id) {
        String selectRating = "select id, name from ratings where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectRating, id);
        return rs.next() ? new Rating(rs.getString("name")
                , rs.getInt("id")) : null;
    }
}
