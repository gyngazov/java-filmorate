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
        jdbcTemplate.update(addUser
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
        );
        user.setId(getLastId("users"));
        return user;
    }

    private int getLastId(String table) {
        String lastId = "select max(id) from " + table;
        SqlRowSet rs = jdbcTemplate.queryForRowSet(lastId);
        return rs.next() ? rs.getInt(1) : 0;
    }

    @Override
    public User updateUser(User user) {
        String updateUser = "update users set email=?, login=?, name=?, birthday=? where id=?";
        jdbcTemplate.update(updateUser
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId()
        );
        return user;
    }

    /**
     * Перед удалением юзера чистить зависящие от него записи.
     */
    @Override
    public void deleteUser(User user) {
        clearFriends(user.getId());
        clearLikes(user.getId());
        String deleteUser = "delete from users where id=?";
        jdbcTemplate.update(deleteUser, user.getId());
    }

    @Override
    public User getUser(int id) {
        String getUser = "select * from users where id=?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(getUser, id);
        User user;
        if (srs.next()) {
            user = new User(
                    id
                    , srs.getString(2)
                    , srs.getString(3)
                    , srs.getString(4)
                    , Objects.requireNonNull(srs.getDate(5)).toLocalDate());
        } else {
            return null;
        }
        String getFriends = "select friend_id from friends "
                + "where user_id=? and is_accepted=true "
                + "union "
                + "select user_id from friends "
                + "where friend_id=? and is_accepted=true";
        srs = jdbcTemplate.queryForRowSet(getFriends, id, id);
        while (srs.next()) {
            user.addFriend(srs.getInt(1));
        }
        return user;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id")
                , rs.getString("email")
                , rs.getString("login")
                , rs.getString("name")
                , rs.getDate("birthday").toLocalDate());
    }

    @Override
    public Collection<User> getUsers() {
        String getUsers = "select * from users";
        return jdbcTemplate.query(getUsers, (rs, rowNum) -> makeUser(rs));
    }

    public Collection<Relation> getFriends() {
        String getFriends = "select user_id, friend_id from friends where is_accepted=true";
        return jdbcTemplate.query(getFriends, (rs, rowNum)
                -> new Relation(rs.getInt(1), rs.getInt(2)));
    }


    @Override
    public void deleteFriend(int userId1, int userId2) {
        String deleteFriend = "delete from friends where user_id=? and friend_id=?";
        jdbcTemplate.update(deleteFriend, userId1, userId2);
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
     * @param userId      id юзера
     * @param is_accepted признак подтверждения дружбы
     * @return список друзей данного юзера
     */
    @Override
    public Collection<User> getFriends(int userId, boolean is_accepted) {
        String selectFriends = "select u.* from friends f "
                + "inner join users u on u.id=f.friend_id "
                + "where f.user_id=? and f.is_accepted=?";
        return jdbcTemplate.query(selectFriends, (rs, rowNum) -> new User(
                rs.getInt(1)
                , rs.getString(2)
                , rs.getString(3)
                , rs.getString(4)
                , rs.getDate(5).toLocalDate()), userId, is_accepted);
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

}
