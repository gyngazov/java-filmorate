package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;
    private int id;

    public InMemoryUserStorage() {
        this.users = new ConcurrentHashMap<>();
        id = 0;
    }
    @Override
    public int setId() {
        return ++id;
    }

    @Override
    public User createUser(User user) throws ValidationException {
        validateUser(user);
        int userId = setId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Создан пользователь " + user);
        return user;
    }
    @Override
    public User updateUser(User user)
            throws ValidationException, ObjectNotFoundException {
        if (user == null) {
            throw new ObjectNotFoundException("Пользователь не задан.");
        }
        int userId = user.getId();
        User oldUser = getUser(userId);
        validateUser(user);
        users.put(userId, user);
        log.info("Пользователь " + oldUser + " изменен на " + user);
        return user;
    }
    @Override
    public void deleteUser(User user)
            throws ValidationException, ObjectNotFoundException {
        if (user == null) {
            log.info("Пользователь = null.");
            return;
        }
        int userId = user.getId();
        for (int id: user.getFriends()) {
            deleteFriend(id, userId);
        }
        users.remove(userId);
        log.info("Пользователь " + user + " удален.");
    }
    @Override
    public void deleteFriend(int userId1, int userId2)
            throws ValidationException, ObjectNotFoundException {
        User user = getUser(userId1);
        user.deleteFriend(userId2);
        updateUser(user);
    }
    @Override
    public void addFriend(int userId1, int userId2)
            throws ObjectNotFoundException, ValidationException {
        User user = getUser(userId1);
        user.addFriend(userId2);
        updateUser(user);
    }
    @Override
    public User getUser(int id) throws ObjectNotFoundException {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь с id " + id + " не найден.");
        } else {
            return users.get(id);
        }
    }
    @Override
    public Collection<User> getUsers() {
        return users.values();
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
