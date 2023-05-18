package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final DbUserStorage userStorage;

    @Test
    public void testFindUserById() {
        String email = "em@il.ru";
        User testUser = new User(111
                , email
                , "l0gin"
                , "name0"
                , LocalDate.of(2002, 11, 11));
        userStorage.createUser(testUser);
        User foundUser = userStorage.getUser(1);
        assertAll(
                () -> assertNotNull(foundUser),
                () -> assertEquals(foundUser.getId(), 1),
                () -> assertEquals(foundUser.getEmail(), email)
        );
    }
}