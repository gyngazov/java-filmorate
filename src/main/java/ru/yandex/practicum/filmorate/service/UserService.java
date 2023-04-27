package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public User createUser(User user) throws ValidationException {
        validateUser(user);
        userStorage.createUser(user);
        log.info("Создан пользователь " + user);
        return user;
    }
    public User updateUser(User user)
            throws ValidationException, ObjectNotFoundException {
        if (user == null) {
            throw new ObjectNotFoundException("Пользователь не задан.");
        }
        validateUser(user);
        User oldUser = getUser(user.getId());
        userStorage.updateUser(user);
        log.info("Пользователь " + oldUser + " обновлен на " + user);
        return user;
    }
    public User getUser(int id) throws ObjectNotFoundException {
        return userStorage.getUser(id);
    }
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public void deleteUser(int id) throws ObjectNotFoundException {
        userStorage.deleteUser(getUser(id));
        log.info("Пользователь с id " + id + " удален.");
    }

    public void addFriend(int userId1, int userId2)
            throws ValidationException, ObjectNotFoundException {
        if (userId1 == userId2) {
            throw new ValidationException("Нельзя добавить себя в друзья.");
        }
        userStorage.addFriend(userId1, userId2);
        userStorage.addFriend(userId2, userId1);
        log.info("Пользователю с id " + userId1 + " добавлен друг с id " + userId2);
    }
    public List<User> getFriends(int id)
            throws ObjectNotFoundException {
        List<User> friends = new ArrayList<>();
        for (Integer fid: userStorage.getUser(id).getFriends()) {
            friends.add(userStorage.getUser(fid));
        }
        return friends;
    }
    public void deleteFriend(int userId1, int userId2) throws ObjectNotFoundException {
        userStorage.deleteFriend(userId1, userId2);
        userStorage.deleteFriend(userId2, userId1);
        log.info("Пользователи с id " + userId1 + "и " + userId2 + " более не друзья.");
    }
    public List<User> getCommonFriends(int userId1, int userId2) throws ObjectNotFoundException {
        Set<Integer> intersection = new HashSet<>(userStorage.getUser(userId1).getFriends());
        intersection.retainAll(userStorage.getUser(userId2).getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (Integer id: intersection) {
            commonFriends.add(userStorage.getUser(id));
        }
        return commonFriends;
    }
    private void validateUser(User user) throws ValidationException {
        if (user.getBirthday() == null) {
            throw new ValidationException("Не задан день рождения.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождения не может быть в будущем.");
        } else if (StringUtils.isBlank(user.getLogin()) || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        } else if (StringUtils.isBlank(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой "
                    + "и должна содержать символ @");
        }
        if (user.getName() == null || StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}
