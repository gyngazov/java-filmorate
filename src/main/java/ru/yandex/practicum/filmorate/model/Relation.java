package ru.yandex.practicum.filmorate.model;

/**
 * Для обмена данными из friends между storage и service.
 */

public class Relation {
    private final int userId;
    private final int friendId;
    private final boolean isAccepted;

    public Relation(int userId, int friendId, boolean isAccepted) {
        this.userId = userId;
        this.friendId = friendId;
        this.isAccepted = isAccepted;
    }

    public int getUserId() {
        return userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                ", isAccepted=" + isAccepted +
                '}';
    }
}
