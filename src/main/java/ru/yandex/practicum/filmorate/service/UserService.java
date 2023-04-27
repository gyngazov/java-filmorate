package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    UserStorage userStorage;
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public User createUser(User user) throws ValidationException {
        return userStorage.createUser(user);
    }
    public User updateUser(User user)
            throws ValidationException, ObjectNotFoundException {
        return userStorage.updateUser(user);
    }
    public User getUser(int id) throws ObjectNotFoundException {
        return userStorage.getUser(id);
    }

    public void addFriend(int userId1, int userId2)
            throws ValidationException, ObjectNotFoundException {
        if (userId1 == userId2) {
            throw new ValidationException("Нельзя добавить себя в друзья.");
        }
        userStorage.addFriend(userId1, userId2);
        userStorage.addFriend(userId2, userId1);
    }
    public List<User> getFriends(int id)
            throws ObjectNotFoundException {
        List<User> friends = new ArrayList<>();
        for (Integer fid: userStorage.getUser(id).getFriends()) {
            friends.add(userStorage.getUser(fid));
        }
        return friends;
    }
    public void deleteFriend(int userId1, int userId2)
            throws ValidationException, ObjectNotFoundException {
        userStorage.deleteFriend(userId1, userId2);
        userStorage.deleteFriend(userId2, userId1);
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
}
