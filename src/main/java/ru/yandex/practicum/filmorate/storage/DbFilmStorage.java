package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

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
                , film.getRating()
        );
        int filmId = getLastId("films");
        insertBatch(filmId, new ArrayList<>(film.getFilmGenres()), "film_genres", "genre_id");
        insertBatch(filmId, new ArrayList<>(film.getUsersLikes()), "likes", "user_id");
        return filmId;
    }

    /**
     * id последней записи в табле.
     * Упрощенное получение id только что вставленной записи.
     *
     * @param table имя таблы
     * @return id
     */
    private int getLastId(String table) {
        String lastId = "select max(id) from " + table;
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    /**
     * Однотипная вставка в лайки или в жанры.
     *
     * @param batch  список id
     * @param table  табла для вставки
     * @param column колонка для вставки списка
     */
    private void insertBatch(int filmId, List<Integer> batch, String table, String column) {
        String insertQuery = "insert into "
                + table
                + "(film_id, "
                + column
                + ") values(?, ?)";
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, batch.get(i));
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

    /**
     * Лайки/жанры:
     * - не обновляются
     * - добавляются/удаляются по одному в отдельных ендпойнтах
     */
    @Override
    public Film updateFilm(Film film) {
        String update = "update films set name=?, description=?, "
                + "release_date=?, duration=?, rating_id=? "
                + "where id=?";
        jdbcTemplate.update(update
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRating()
                , film.getId()
        );
        return film;
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
     * Получение лайков либо жанров ввиду единообразия хранения.
     *
     * @param tabName имя таблицы хранения
     * @return найденные id в виде сета
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
        Collection<Film> films = jdbcTemplate
                .query(queryFilms, (rs, rowNum) -> new Film(
                        rs.getInt("id")
                        , rs.getString("name")
                        , rs.getString("description")
                        , rs.getDate("release_date").toLocalDate()
                        , rs.getInt("duration")
                        , rs.getInt("rating_id")));
        for (Film f : films) {
            f.setLikes(getFilmLikes(f.getId()));
            f.setGenres(getFilmGenres(f.getId()));
        }
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
        String selectLikes = "select u.id, u.email, u.login, u.name, u.birthday from likes l"
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
        String topFilms = "select f.*"
                + "from films f"
                + "inner join likes l on"
                + "l.film_id=f.id"
                + "group by f.id, f.name"
                + "order by count(l.id) desc"
                + "limit ?";
        return jdbcTemplate.query(topFilms, (rs, rowNum) -> new Film(
                rs.getInt(1)
                , rs.getString(2)
                , rs.getString(3)
                , rs.getDate(4).toLocalDate()
                , rs.getInt(5)
                , rs.getInt(6)), top);
    }

    @Override
    public int createGenre(Genre genre) {
        String insertGenre = "insert into genres(name) values(?)";
        jdbcTemplate.update(insertGenre, genre.getName());
        return getLastId("genres");
    }

    @Override
    public int createRating(Rating rating) {
        String insertGenre = "insert into ratings(name) values(?)";
        jdbcTemplate.update(insertGenre, rating.getName());
        return getLastId("ratings");
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

    private Collection<Genre> getFilmGenres(int filmId) {
        String selectGenres = "select g.id, g.name from film_genres f"
                + "inner join genres g on g.id=f.genre_id "
                + "where f.film_id=?";
        return jdbcTemplate.query(selectGenres
                , (rs, rowNum) -> new Genre(rs.getString("name")
                        , rs.getInt("id")), filmId);
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
