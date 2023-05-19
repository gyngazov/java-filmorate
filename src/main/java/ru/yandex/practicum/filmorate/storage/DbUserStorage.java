package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

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
        return user;
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

    @Override
    public void deleteUser(User user) {
        String deleteUser = "delete from users where id=?";
        jdbcTemplate.update(deleteUser, user.getId());
    }

    /**
     * usersFriends - мапа друзей юзера.
     * Вспомогательный метод:
     * - сбор мапы друзей по юзерам.
     * - запись друзей в соответствующих юзеров
     *
     * @return users:
     * - при id>0 содержит один элемент (если такой есть в users)
     * - при id=0 содержит все записи users
     */
    private Collection<User> getUserById(int id) {
        Map<User, Set<Integer>> usersFriends = new HashMap<>();
        String uid = id > 0 ? "u.id" : "0";
        String userById = "select u.id, u.email, u.login, u.name, u.birthday, f.friend_id "
                + "from users u "
                + "left outer join friends f on "
                + "u.id=f.user_id where " + uid + "=?";
        jdbcTemplate.query(userById, (rs, rowNum) -> makeFriend(rs, usersFriends), id);
        Collection<User> users = new ArrayList<>();
        for (User u : usersFriends.keySet()) {
            u.setFriends(usersFriends.get(u));
            users.add(u);
        }
        return users;
    }

    @Override
    public User getUser(int id) {
        return getUserById(id)
                .stream()
                .findFirst()
                .orElse(null);
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
        return getUserById(0);
    }

    /**
     * @param friendsMap мапа друзей юзера
     */
    private User makeFriend(ResultSet rs, Map<User, Set<Integer>> friendsMap) throws SQLException {
        User user = makeUser(rs);
        Set<Integer> firstFriend;
        int friendId = rs.getInt("friend_id");
        if (rs.wasNull()) {
            // null при left join будет единственный,
            // поэтому когда встретится такой rs,
            // то записи с данным user в мапе еще нет
            firstFriend = new HashSet<>();
        } else {
            firstFriend = new HashSet<>(friendId);
        }
        if (friendsMap.containsKey(user)) {
            friendsMap.get(user).add(friendId);
        } else {
            friendsMap.put(user, firstFriend);
        }
        return user;
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
    public void clearDb() {
        Stream
                .of("likes"
                        , "friends"
                        , "users"
                        , "film_genres"
                        , "films"
                        , "genres"
                        , "ratings")
                .map(t -> "delete from " + t)
                .forEach(jdbcTemplate::update);
    }
}
