package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.util.Collection;

public interface UserStorage {
    int setId();

    User createUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException;

    void deleteUser(User user) throws ValidationException;

    User getUser(int id) throws ValidationException;

    Collection<User> getUsers();

    void deleteFriend(int userId1, int userId2) throws ValidationException;

    void addFriend(int userId1, int userId2) throws ValidationException;
}
