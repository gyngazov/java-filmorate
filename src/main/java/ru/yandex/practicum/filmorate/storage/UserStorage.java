package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Relation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    int setId();

    User createUser(User user);

    User updateUser(User user) throws ObjectNotFoundException;

    int deleteUser(User user) throws ObjectNotFoundException;

    User getUser(int id) throws ObjectNotFoundException;

    Collection<User> getFriends(int userId);

    Collection<User> getUsers();

    Collection<Relation> getAllFriends();

    int deleteFriend(int userId1, int userId2) throws ObjectNotFoundException;

    void addFriend(int userId1, int userId2) throws ObjectNotFoundException;

    int acceptFriendship(int userId1, int userId2);

    boolean isExisting(String table, int id);
}
