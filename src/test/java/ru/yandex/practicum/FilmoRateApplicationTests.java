package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final DbUserStorage userStorage;

    @Test
    public void testFindUserById() {
        String email = "em@il.ru";
        User testUser = new User(111,
                email,
                "l0gin",
                "name0",
                LocalDate.of(2002, 11, 11));
        userStorage.createUser(testUser);
        int testId = 11;
        User foundUser = userStorage.getUser(1);
        assertAll(
                () -> assertNotNull(foundUser, "Не найден пользователь с id " + testId),
                () -> assertEquals(foundUser.getId(), 1, "Найден пользователь с id != " + testId),
                () -> assertEquals(foundUser.getEmail(), email)
        );
    }
}