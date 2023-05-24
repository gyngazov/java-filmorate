package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * Удалять юзера из друзей у его друзей.
     */
    @Override
    public int deleteUser(User user) {
        for (int id : user.getFriends()) {
            deleteFriend(id, user.getId());
        }
        users.remove(user.getId());
        return 0;
    }

    /**
     * Проверять, что на удаление прислали id из бд.
     */
    @Override
    public int deleteFriend(int userId1, int userId2) {
        User user = getUser(userId1);
        getUser(userId2);
        user.deleteFriend(userId2);
        return 0;
    }

    /**
     * Проверять, что на добавление прислали id из бд.
     */
    @Override
    public void addFriend(int userId1, int userId2) {
        User user = getUser(userId1);
        getUser(userId2);
        user.addFriend(userId2);
    }

    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("Пользователь с id " + id + " не найден.");
        } else {
            return users.get(id);
        }
    }

    @Override
    public Collection<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public Collection<Relation> getAllFriends() {
        return null;
    }

    @Override
    public int acceptFriendship(int userId1, int userId2) {

        return userId1;
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        return null;
    }

}
