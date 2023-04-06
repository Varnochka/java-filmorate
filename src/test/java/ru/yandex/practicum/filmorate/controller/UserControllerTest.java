package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.imp.InMemoryUserStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private static Validator validator;
    private User user;

    private UserController userController;

    @BeforeAll
    static void beforeAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void init() {
        user = new User(null, "user@mail.ru", "userLogin", "userName", LocalDate.now());
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void createUser_rejectEmail_emailIsNull() {
        user.setEmail(null);
        assertEquals(1, validator.validate(user).size());

    }

    @Test
    void createUser_rejectEmail_emailIsEmpty() {
        user.setEmail("");
        assertEquals(1, validator.validate(user).size());

    }

    @Test
    void createUser_acceptEmail_emailIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void createUser_rejectLogin_loginIsNull() {
        user.setLogin(null);
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void createUser_rejectLogin_loginIsEmpty() {
        user.setLogin("");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void createUser_acceptLogin_loginIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void createUser_rejectBirthday_birthdayIsFuture() {
        user.setBirthday(LocalDate.now().plusDays(10));
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void updateUser_validationException_userWithIdIsNull() {
        userController.createUser(user);

        user.setId(null);
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void updateUser_validationException_userWithIdIsIncorrect() {
        userController.createUser(user);

        user.setId(-5);
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void addUser_validationException_userWithLoginIsNotUnique() {
        userController.createUser(user);

        User newUser = new User(null, "user1@mail.ru", "userLogin", "userName", LocalDate.now());

        assertThrows(ValidationException.class, () -> userController.updateUser(newUser));
    }

    @Test
    void updateUser_noSuchUserException_userIdIsNotExist() {
        userController.createUser(user);

        User newUser = new User(10, "user@mail.ru", "userLogin1", "userName", LocalDate.now());

        assertThrows(NoSuchUserException.class, () -> userController.updateUser(newUser));
    }
}