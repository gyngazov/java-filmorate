package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        validateUser(user);
        userStorage.createUser(user);
        log.info("Создан пользователь {}.", user);
        return user;
    }

    public User updateUser(User user) {
        validateUser(user);
        User oldUser = getUser(user.getId());
        userStorage.updateUser(user);
        log.info("Пользователь {} обновлен на {}.", oldUser, user);
        return user;
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public Collection<User> getUsers() {
        users.forEach(u -> u.setFriends(friends.get(u.getId())));
        return users;
        return userStorage.getUsers();
    }

    /**
     * Сбор мапы друзей по результату селекта.
     * Одобренная дружба создает друзей взаимно.
     *
     * @return мапа между юзером и сетом его друзей
     */
    private Map<Integer, Set<Integer>> collectFriends(SqlRowSet srs) {
        Map<Integer, Set<Integer>> friends = new HashMap<>();
        int userId;
        int friendId;
        while (srs.next()) {
            userId = srs.getInt(1);
            friendId = srs.getInt(2);
            if (friends.containsKey(userId)) {
                friends.get(userId).add(friendId);
                friends.get(friendId).add(userId);
                System.out.println("friends c= " + friends);
            } else {
                friends.put(userId, new HashSet<>(friendId));
                friends.put(friendId, new HashSet<>(userId));
                System.out.println("friends n= " + friends);
            }
        }
        System.out.println("friends r= " + friends);
        return friends;
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(getUser(id));
        log.info("Пользователь с id {} удален.", id);
    }

    public void addFriend(int userId1, int userId2) {
        if (userId1 == userId2) {
            throw new ValidationException("Нельзя добавить себя в друзья.");
        }
        userStorage.addFriend(userId1, userId2);
        log.info("Пользователю с id {} добавлен друг с id {}.", userId1, userId2);
    }

    public List<User> getFriends(int id) {
        return userStorage
                .getUser(id)
                .getFriends()
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public void deleteFriend(int userId1, int userId2) {
        userStorage.deleteFriend(userId1, userId2);
        log.info("Пользователи с id {} и {} более не друзья.", userId1, userId2);
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        Set<User> intersection = new HashSet<>(getFriends(userId1));
        intersection.retainAll(getFriends(userId2));
        return new ArrayList<>(intersection);
    }

    private void validateUser(User user) {
        if (user.getName() == null || StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public void acceptFriendship(int userId1, int userId2) {
        if (userStorage
                .getFriends(userId1, false)
                .stream()
                .anyMatch(u -> u.getId() == userId2)) {
            userStorage.acceptFriendship(userId1, userId2);
            log.info("Пользователь " + userId1 + " подтвердил дружбу пользователю " + userId2);
        } else {
            throw new ValidationException("Дружба не запрашивалась.");
        }
    }
}
