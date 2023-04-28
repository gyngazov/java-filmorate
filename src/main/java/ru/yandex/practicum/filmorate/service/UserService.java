package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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
        return userStorage.getUsers();
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
        userStorage.addFriend(userId2, userId1);
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
        userStorage.deleteFriend(userId2, userId1);
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
}
