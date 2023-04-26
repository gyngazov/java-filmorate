package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.*;

@Data
public class User {
    int id;
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
    Set<Integer> friends;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        friends = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getEmail().equals(user.getEmail())
                && Objects.equals(getLogin(), user.getLogin())
                && getName().equals(user.getName())
                && getBirthday().equals(user.getBirthday());
    }
    public void addFriend(int id) {
        friends.add(id);
    }
    public List<Integer> getFriendsIds() {
        return new ArrayList<>(friends);
    }
    public void deleteFriend(int id) {
        friends.remove(id);
    }
}
