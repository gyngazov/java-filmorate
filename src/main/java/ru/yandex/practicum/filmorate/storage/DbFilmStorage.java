package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int setId() {
        return 0;
    }

    /**
     * В create-ах ниже возвращаем id вставленной записи.
     *
     * @return id вставленной записи
     * По фильму вставляем пачки лайков и жанров.
     */
    @Override
    public int createFilm(Film film) {
        String insertFilm = "insert into films(name, description, release_date, duration, rating_id) "
                + "values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertFilm
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
        );
        int filmId = getLastId();
        if (film.getGenres() != null) {
            insertBatch(filmId, new ArrayList<>(film.getGenres()));
        }
        return filmId;
    }

    /**
     * id последней записи в films.
     * Упрощенное получение id только что вставленной записи.
     *
     * @return id
     */
    private int getLastId() {
        String lastId = "select max(id) from films";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    /**
     * Однотипная вставка в жанры.
     *
     * @param batch список id
     */
    private void insertBatch(int filmId, List<Genre> batch) {
        String insertQuery = "insert into film_genres (film_id, genre_id) values(?, ?)";
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, batch.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
    }

    @Override
    public void addFilmGenre(int filmId, int genreId) {
        String insertFilmGenre = "insert into film_genres(film_id, genre_id) values(?, ?)";
        jdbcTemplate.update(insertFilmGenre, filmId, genreId);
    }

    @Override
    public void deleteFilmGenre(int filmId, int genreId) {
        String deleteFilmGenre = "delete from film_genres where film_id=? and genre_id=?";
        jdbcTemplate.update(deleteFilmGenre, filmId, genreId);
    }

    /**
     * Обновить вместе с жанрами.
     */
    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        Film filmBefore = getFilm(filmId);
        String update = "update films set name=?, description=?, "
                + "release_date=?, duration=?, rating_id=? "
                + "where id=?";
        jdbcTemplate.update(update
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , filmId
        );
        if (film.getGenres() != null) {
            updateFilmGenres(filmId, filmBefore.getGenres(), film.getGenres());
        }
        return getFilm(filmId);
    }

    /**
     * Обновить жанры по простому:
     * - удалить жанры из бд
     * - вставить жанры из json
     * - по одной записи
     * - без транзакции
     *
     * @param genresBefore жанры бд
     * @param genresAfter  жанры json
     */
    private void updateFilmGenres(int filmId, Collection<Genre> genresBefore, Collection<Genre> genresAfter) {
        for (Genre g : genresBefore) {
            deleteFilmGenre(filmId, g.getId());
        }
        genresAfter
                .stream()
                .map(Genre::getId)
                .distinct()
                .forEach(id -> addFilmGenre(filmId, id));
    }

    @Override
    public Film getFilm(int filmId) {
        String queryFilm = "select * from films where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(queryFilm, filmId);
        Film film = rs.next() ? new Film(
                rs.getInt(1)
                , rs.getString(2)
                , rs.getString(3)
                , Objects.requireNonNull(rs.getDate(4)).toLocalDate()
                , rs.getInt(5)
                , getMpa(rs.getInt(6))
                , null) : null;
        if (film == null) {
            return null;
        }
        film.setGenres(getFilmGenres(filmId));
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        String queryFilms = "select * from films";
        Collection<Film> films = jdbcTemplate
                .query(queryFilms, (rs, rowNum) -> new Film(
                        rs.getInt("id")
                        , rs.getString("name")
                        , rs.getString("description")
                        , rs.getDate("release_date").toLocalDate()
                        , rs.getInt("duration")
                        , getMpa(rs.getInt("rating_id"))
                        , getFilmGenres(rs.getInt(1))));
        films.forEach(f -> f.setLikes(getFilmLikes(f.getId())));
        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String insertLike = "insert into likes (film_id, user_id) values(?, ?)";
        jdbcTemplate.update(insertLike, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String deleteLike = "delete from likes where film_id=? and user_id=?";
        jdbcTemplate.update(deleteLike, filmId, userId);
    }

    private Collection<User> getFilmLikes(int filmId) {
        String selectLikes = "select u.* from likes l "
                + "inner join users u on u.id=l.user_id "
                + "where l.film_id=?";
        return jdbcTemplate.query(selectLikes, (rs, rowNum) -> new User(
                rs.getInt("id")
                , rs.getString("email")
                , rs.getString("login")
                , rs.getString("name")
                , rs.getDate("birthday").toLocalDate()), filmId);
    }

    @Override
    public int getFilmsCount() {
        String count = "select count(id) from films";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(count);
        return rs.next() ? rs.getInt(1) : 0;
    }

    @Override
    public List<Film> getFilmsByPopularity(int top) {
        String topFilms =
                "select f.id, f.name, f.description, f.release_date, f.duration, f.rating_id "
                        + "from films f "
                        + "left outer join likes l on "
                        + "l.film_id=f.id "
                        + "group by f.id, f.name, f.description, f.release_date, f.duration, f.rating_id "
                        + "order by sum(case when l.id is null then 0 else 1 end) desc "
                        + "limit ?";
        return jdbcTemplate.query(topFilms, (rs, rowNum) -> new Film(
                        rs.getInt(1)
                        , rs.getString(2)
                        , rs.getString(3)
                        , rs.getDate(4).toLocalDate()
                        , rs.getInt(5)
                        , getMpa(rs.getInt(6))
                        , getFilmGenres(rs.getInt(1)))
                , top);
    }

    @Override
    public Genre getGenre(int id) {
        String selectGenre = "select * from genres where id=?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectGenre, id);
        Genre genre;
        if (rs.next()) {
            genre = new Genre();
        } else {
            return null;
        }
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    @Override
    public Collection<Genre> getGenres() {
        String selectGenres = "select * from genres order by id";
        return jdbcTemplate.query(selectGenres, (rs, rowNum) -> setGenre(rs));
    }

    private Collection<Genre> getFilmGenres(int filmId) {
        String selectGenres = "select g.id, g.name from film_genres f "
                + "inner join genres g on g.id=f.genre_id "
                + "where f.film_id=?";
        return jdbcTemplate.query(selectGenres, (rs, rowNum) -> setGenre(rs), filmId);
    }

    private Genre setGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    @Override
    public Collection<Mpa> getMpas() {
        String selectRatings = "select id, name from ratings order by id";
        return jdbcTemplate.query(selectRatings, (rs, rowNum) -> setMpa(rs));
    }

    private Mpa setMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt(1));
        mpa.setName(rs.getString(2));
        return mpa;
    }

    @Override
    public Mpa getMpa(int id) {
        String selectRating = "select id, name from ratings where id=?";
        return jdbcTemplate
                .query(selectRating, (rs, rowNum) -> setMpa(rs), id)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
