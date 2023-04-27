package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) throws ObjectNotFoundException {
        return userService.getUser(id);
    }
    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user)
            throws ValidationException, ObjectNotFoundException {
        return userService.updateUser(user);
    }
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId)
            throws ValidationException, ObjectNotFoundException {
        userService.addFriend(id, friendId);
    }
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) throws ObjectNotFoundException {
        return userService.getFriends(id);
    }
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId)
            throws ObjectNotFoundException {
        return userService.getCommonFriends(id, otherId);
    }
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId)
            throws ValidationException, ObjectNotFoundException {
        userService.deleteFriend(id, friendId);
    }
}
