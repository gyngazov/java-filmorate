package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
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
    public User createUser(User user) {
        int userId = setId();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }
    @Override
    public User updateUser(User user) {
        return users.put(user.getId(), user);
    }
    @Override
    public void deleteUser(User user)
            throws ObjectNotFoundException {
        for (int id: user.getFriends()) {
            deleteFriend(id, user.getId());
        }
        users.remove(user.getId());

    }
    @Override
    public void deleteFriend(int userId1, int userId2) throws ObjectNotFoundException {
        User user = getUser(userId1);
        user.deleteFriend(userId2);
        updateUser(user);
    }
    @Override
    public void addFriend(int userId1, int userId2) throws ObjectNotFoundException {
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
}
