package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.util.Collection;

public interface UserStorage {
    int setId();

    User createUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException, ObjectNotFoundException;

    void deleteUser(User user) throws ValidationException, ObjectNotFoundException;

    User getUser(int id) throws ObjectNotFoundException;

    Collection<User> getUsers();

    void deleteFriend(int userId1, int userId2) throws ValidationException, ObjectNotFoundException;

    void addFriend(int userId1, int userId2) throws ValidationException, ObjectNotFoundException;
}
