package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int setId() {
        return 0;
    }

    @Override
    public User createUser(User user) {
        String addUser = "insert into users (email, login, name, birthday) values(?, ?, ?, ?)";
        jdbcTemplate.update(addUser,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(getLastId());
        return user;
    }

    private int getLastId() {
        String lastId = "select max(id) from users";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    @Override
    public User updateUser(User user) {
        String updateUser = "update users set email=?, login=?, name=?, birthday=? where id=?";
        jdbcTemplate.update(updateUser,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    /**
     * Перед удалением юзера чистить зависящие от него записи.
     */
    @Override
    public int deleteUser(User user) {
        clearFriends(user.getId());
        clearLikes(user.getId());
        String deleteUser = "delete from users where id=?";
        return jdbcTemplate.update(deleteUser, user.getId());
    }

    @Override
    public User getUser(int id) {
        String getUser = "select * from users where id=?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(getUser, id);
        if (srs.next()) {
            return new User(
                    id,
                    srs.getString(2),
                    srs.getString(3),
                    srs.getString(4),
                    Objects.requireNonNull(srs.getDate(5)).toLocalDate());
        } else {
            return null;
        }
    }

    /**
     * Сбор int-ов друзей юзера.
     */
    @Override
    public Collection<User> getFriends(int userId) {
        String getFriends =
                "select * from users where id in "
                        // мои друзья - это и подтвержденные и не подтвержденные
                        + "(select friend_id from friends "
                        + "where user_id=? "
                        + "union "
                        // учесть тех, кому я подтвердил дружбу
                        + "select user_id from friends "
                        + "where friend_id=? and is_accepted=true)";
        return jdbcTemplate.query(getFriends, (rs, rowNum) -> makeUser(rs), userId, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
    }

    @Override
    public Collection<User> getUsers() {
        String getUsers = "select * from users";
        return jdbcTemplate.query(getUsers, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Collection<Relation> getAllFriends() {
        String getFriends = "select user_id, friend_id, is_accepted from friends";
        return jdbcTemplate.query(getFriends, (rs, rowNum)
                -> new Relation(rs.getInt(1), rs.getInt(2), rs.getBoolean(3)));
    }


    @Override
    public int deleteFriend(int userId1, int userId2) {
        String deleteFriend = "delete from friends where user_id=? and friend_id=?";
        return jdbcTemplate.update(deleteFriend, userId1, userId2);
    }

    @Override
    public void addFriend(int userId1, int userId2) {
        String addFriend = "insert into friends (user_id, friend_id) values(?, ?)";
        jdbcTemplate.update(addFriend, userId1, userId2);
    }

    @Override
    public int acceptFriendship(int userId1, int userId2) {
        String accept = "update friends set is_accepted=true "
                + "where user_id=? and friend_id=? and is_accepted=false";
        return jdbcTemplate.update(accept, userId1, userId2);
    }

    /**
     * Очистить friends от userId для подготовки удаления данного юзера.
     */
    private void clearFriends(int userId) {
        String deleteFriends = "delete from friends where user_id=? or friend_id=?";
        jdbcTemplate.update(deleteFriends, userId, userId);
    }

    /**
     * Очистить likes от userId для подготовки удаления данного юзера.
     */
    private void clearLikes(int userId) {
        String deleteLikes = "delete from likes where user_id=?";
        jdbcTemplate.update(deleteLikes, userId);
    }

    public boolean isExisting(String table, int id) {
        String find = "select (exists (select 1 from " + table + " where id=?))";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(find, id);
        srs.next();
        return srs.getBoolean(1);
    }

}
