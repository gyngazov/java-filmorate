package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Relation;
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

    /**
     * Сбор всех юзеров.
     * В два запроса:
     * - к users
     * - к friends
     * По friends сбор мапы.
     *
     * @return список объектов User
     */
    public Collection<User> getUsers() {
        Collection<User> users = userStorage.getUsers();
        Map<Integer, Set<Integer>> friends = collectFriends(userStorage.getTrueFriends());
        users.forEach(u -> u.setFriends(friends.get(u.getId())));
        return users;
    }

    /**
     * Сбор мапы друзей по результату селекта.
     * Одобренная дружба создает друзей взаимно.
     *
     * @return мапа между юзером и сетом его друзей
     */
    private Map<Integer, Set<Integer>> collectFriends(Collection<Relation> records) {
        Map<Integer, Set<Integer>> friends = new HashMap<>();
        for (Relation record : records) {
            if (record.isAccepted()) {
                // подтвержденный друг дописывается к подтвердившему
                insertPair(friends, record.getFriendId(), record.getUserId());
            }
            // подтвержденный или не подтвержденный друг дописывается к запросившему подтверждения
            insertPair(friends, record.getUserId(), record.getFriendId());
        }
        return friends;
    }

    /**
     * Вспомогательный - вставка пары в мапу.
     *
     * @param friends  мапа
     * @param userId   друг 1
     * @param friendId друг 2
     */
    private void insertPair(Map<Integer, Set<Integer>> friends, int userId, int friendId) {
        if (!friends.containsKey(userId)) {
            friends.put(userId, new HashSet<>());
        }
        friends.get(userId).add(friendId);
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
