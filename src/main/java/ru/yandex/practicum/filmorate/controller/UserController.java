package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int idUser = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Request add new User");

        if (!users.values()
                .stream()
                .noneMatch(userSaved ->
                        userSaved.getLogin().equals(user.getLogin())
                                && userSaved.getEmail().equals(user.getEmail()))) {
            log.error("User with login {} already exist", user.getLogin());
            throw new ValidationException("User with login='" + user.getId() + "' already exist");
        }
        user.setId(idUser++);

        if(StringUtils.hasLength(user.getName()) && StringUtils.hasText(user.getName())){
            user.setName(user.getName());
        }else {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        log.info("Successful added new user {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Request update user");

        Integer userId = user.getId();

        if (!users.containsKey(userId)) {
            log.error("User with id='" + user.getId() + "' is not exist");
            throw new ValidationException("Invalid user id='" + user.getId() + "' of updatable user");
        }

        if (userId == null || userId <= 0) {
            log.error("Id updatable user must not be null or less than 1");
            throw new ValidationException("Invalid id='" + userId + "' of updatable user");
        }

        users.put(user.getId(), user);
        log.info("Updatable user {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Returned get all users");
        return new ArrayList<>(users.values());
    }
}